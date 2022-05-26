package com.wushiyii.core.invocation;


import com.wushiyii.core.cache.NodeCache;
import com.wushiyii.core.loadbalance.LoadBalanceUtil;
import com.wushiyii.core.model.NodeInfo;
import com.wushiyii.core.model.RpcRequest;
import com.wushiyii.core.model.RpcResponse;
import com.wushiyii.core.netty.ClientRequestHandlerMap;
import com.wushiyii.core.netty.handler.ClientRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
public class RpcInvocationHandler {


    public static RpcResponse invoke(RpcRequest request) {

        RpcResponse response = new RpcResponse();
        response.setCommandId(request.getCommandId());

        List<NodeInfo> nodeList = NodeCache.getNodeListByProviderName(request.getProviderName());
        if (CollectionUtils.isEmpty(nodeList)) {
            log.error("No rpc provider:{} available; request={}", request.getProviderName(), request);
            throw new RuntimeException("No rpc provider:" + request.getProviderName() + "available");
        }

        //load balance
        NodeInfo selectNode = LoadBalanceUtil.select(nodeList);

        //netty client send
        ClientRequestHandler requestHandler = ClientRequestHandlerMap.getHandler(selectNode);
        return requestHandler.doRequest(request);
    }


}
