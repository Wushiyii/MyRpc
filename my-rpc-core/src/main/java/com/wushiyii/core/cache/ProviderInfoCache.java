package com.wushiyii.core.cache;

import com.wushiyii.core.model.ProviderInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProviderInfoCache {

    private static final Map<String, ProviderInfo> PROVIDER_CACHE = new ConcurrentHashMap<>(256);

    public static void inject(String providerName, ProviderInfo providerInfo) {
        PROVIDER_CACHE.put(providerName, providerInfo);
    }

    public static ProviderInfo getByProviderName(String providerName) {
        return PROVIDER_CACHE.get(providerName);
    }

}
