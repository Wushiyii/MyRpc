package com.wushiyii.core.loadbalance;


import com.wushiyii.core.model.NodeInfo;
import com.wushiyii.core.model.RpcConfig;

import java.util.List;

public class LoadBalanceUtil {

    private static LoadBalancer loadBalancer;

    public static void init(RpcConfig rpcConfig) {
        LoadBalanceUtil.loadBalancer = LoadBalancerFactory.getLoadBalancerByName(rpcConfig.getLoadBalance());
    }

    public static NodeInfo select(List<NodeInfo> nodeList) {
        return loadBalancer.select(nodeList);
    }


}
