package com.wushiyii.core.cache;

import com.wushiyii.core.model.MethodInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MethodInfoCache {

    private static final Map<String, MethodInfo> METHOD_CACHE = new ConcurrentHashMap<>(256);

    public static void inject(String methodName, MethodInfo methodInfo) {
        METHOD_CACHE.put(methodName, methodInfo);
    }

    public static MethodInfo getByMethodName(String methodName) {
        return METHOD_CACHE.get(methodName);
    }

}
