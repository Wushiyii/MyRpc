package com.wushiyii.core.cache;

import com.wushiyii.core.model.NodeInfo;
import com.wushiyii.core.registry.Registry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NodeCache {

    private static Registry registry;

    public static void init(Registry registry) {
        NodeCache.registry = registry;
    }

    private static final Map<String, List<NodeInfo>> NODE_MAP = new ConcurrentHashMap<>(128);

    public static void putNodeList(String providerName, List<NodeInfo> nodeInfoList) {
        log.info("putNodeList, providerName={}, nodeList={}", providerName, nodeInfoList);
        NODE_MAP.put(providerName, nodeInfoList);
    }

    public static List<NodeInfo> getNodeListByProviderName(String providerName) {
        if (NODE_MAP.containsKey(providerName)) {
            return NODE_MAP.get(providerName);
        }
        return subscribe0(providerName);
    }

    private static List<NodeInfo> subscribe0(String providerName) {

        registry.subscribeProvider(providerName);

        return registry.getNodeListByProviderName(providerName);
    }


}
