package com.wushiyii.core.netty;


import com.wushiyii.core.model.NodeInfo;
import com.wushiyii.core.netty.handler.ClientRequestHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRequestHandlerMap {

    private static final Map<String, ClientRequestHandler> CLIENT_MAP = new ConcurrentHashMap<>(128);

    public static ClientRequestHandler getHandler(NodeInfo selectNode) {
        String address = selectNode.getNodeIp() + ":" + selectNode.getNodePort();
        if (!CLIENT_MAP.containsKey(address)) {
            CLIENT_MAP.put(address, new ClientRequestHandler(selectNode));
        }

        return CLIENT_MAP.get(address);
    }



}
