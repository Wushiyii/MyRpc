package com.wushiyii.cache;

import com.wushiyii.model.NodeInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NodeCache {

    private static final Map<String, List<NodeInfo>> NODE_MAP = new ConcurrentHashMap<>(128);

    public static void putNodeList(String methodName, List<NodeInfo> nodeInfoList) {
        log.info("putNodeList, method={}, nodeList={}", methodName, nodeInfoList);
        NODE_MAP.put(methodName, nodeInfoList);
    }

    public static List<NodeInfo> getNodeMapByMethodName(String methodName) {
        return NODE_MAP.get(methodName);
    }


}
