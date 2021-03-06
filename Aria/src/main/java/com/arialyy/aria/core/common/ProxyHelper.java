/*
 * Copyright (C) 2016 AriaLyy(https://github.com/AriaLyy/Aria)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arialyy.aria.core.common;

import com.arialyy.aria.core.AriaManager;
import com.arialyy.aria.util.CommonUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Aria.Lao on 2017/7/10.
 * 代理参数获取
 */
public class ProxyHelper {
  public Set<String> downloadCounter = new HashSet<>(), uploadCounter = new HashSet<>(),
      downloadGroupCounter = new HashSet<>(), downloadGroupSubCounter = new HashSet<>();

  public static volatile ProxyHelper INSTANCE = null;

  private ProxyHelper() {
    init();
  }

  public static ProxyHelper getInstance() {
    if (INSTANCE == null) {
      synchronized (AriaManager.LOCK) {
        INSTANCE = new ProxyHelper();
      }
    }
    return INSTANCE;
  }

  private void init() {
    List<String> classes = CommonUtil.getClassName(AriaManager.APP,
        "com.arialyy.aria.ProxyClassCounter");
    for (String className : classes) {
      count(className);
    }
  }

  private void count(String className) {
    try {
      Class clazz = Class.forName(className);
      Method download = clazz.getMethod("getDownloadCounter");
      Method downloadGroup = clazz.getMethod("getDownloadGroupCounter");
      Method downloadGroupSub = clazz.getMethod("getDownloadGroupSubCounter");
      Method upload = clazz.getMethod("getUploadCounter");
      Object object = clazz.newInstance();
      Object dc = download.invoke(object);
      if (dc != null) {
        downloadCounter.addAll((Set<String>) dc);
      }
      Object dgc = downloadGroup.invoke(object);
      if (dgc != null) {
        downloadGroupCounter.addAll((Set<String>) dgc);
      }
      Object dgsc = downloadGroupSub.invoke(object);
      if (dgsc != null) {
        downloadGroupSubCounter.addAll((Set<String>) dgsc);
      }
      Object uc = upload.invoke(object);
      if (uc != null) {
        uploadCounter.addAll((Set<String>) uc);
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
