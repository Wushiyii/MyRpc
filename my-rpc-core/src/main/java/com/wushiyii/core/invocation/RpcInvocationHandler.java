package com.wushiyii.core.invocation;


import com.wushiyii.core.cache.NodeCache;
import com.wushiyii.core.loadbalance.LoadBalanceUtil;
import com.wushiyii.core.model.NodeInfo;
import com.wushiyii.core.model.RpcRequest;
import com.wushiyii.core.model.RpcResponse;
import com.wushiyii.core.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RpcInvocationHandler {

    private static final Map<String, NettyClient> CLIENT_MAP = new ConcurrentHashMap<>(128);

    public static NettyClient getClient(NodeInfo selectNode) {
        String address = selectNode.toAddress();
        if (!CLIENT_MAP.containsKey(address)) {
            CLIENT_MAP.put(address, new NettyClient(selectNode));
        }

        return CLIENT_MAP.get(address);
    }

    public static void invalidChannel(String address) {
        CLIENT_MAP.remove(address);
    }

    public static RpcResponse invoke(RpcRequest request) {
        log.info("RpcInvocationHandler doInvoke, request={}", request);

        List<NodeInfo> nodeList = NodeCache.getNodeListByProviderName(request.getProviderName());
        if (CollectionUtils.isEmpty(nodeList)) {
            log.error("No rpc provider:{} available; request={}", request.getProviderName(), request);
            throw new RuntimeException("No rpc provider:" + request.getProviderName() + "available");
        }

        //load balance
        NodeInfo selectNode = LoadBalanceUtil.select(nodeList);

        //netty client send
        NettyClient requestHandler = getClient(selectNode);

        return requestHandler.invokeSync(request);
    }


}
