package com.wushiyii.core.loadbalance.impl;

import com.wushiyii.core.loadbalance.LoadBalancer;
import com.wushiyii.core.model.NodeInfo;

import java.util.List;


public class RoundRobinLoadBalancer implements LoadBalancer {

    @Override
    public NodeInfo select(List<NodeInfo> nodeList) {
        return null;
    }
}
