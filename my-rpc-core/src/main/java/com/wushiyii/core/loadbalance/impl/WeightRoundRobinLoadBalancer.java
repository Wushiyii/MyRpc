package com.wushiyii.core.loadbalance.impl;

import com.wushiyii.core.loadbalance.LoadBalanceType;
import com.wushiyii.core.loadbalance.LoadBalancer;
import com.wushiyii.core.model.NodeInfo;

import java.util.List;


public class WeightRoundRobinLoadBalancer implements LoadBalancer {

    @Override
    public String type() {
        return LoadBalanceType.WEIGHT_ROUND_ROBIN;
    }

    @Override
    public NodeInfo select(List<NodeInfo> nodeList) {
        return null;
    }
}
