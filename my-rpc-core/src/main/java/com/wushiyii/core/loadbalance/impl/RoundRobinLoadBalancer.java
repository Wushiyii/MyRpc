package com.wushiyii.core.loadbalance.impl;

import com.wushiyii.core.loadbalance.LoadBalanceType;
import com.wushiyii.core.loadbalance.LoadBalancer;
import com.wushiyii.core.model.NodeInfo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class RoundRobinLoadBalancer implements LoadBalancer {

    private static final int MASK = 0x7FFFFFFF; // avoid negative
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public String type() {
        return LoadBalanceType.ROUND_ROBIN;
    }

    @Override
    public NodeInfo select(List<NodeInfo> nodeList) {

        int index = (atomicInteger.getAndIncrement() & MASK) % nodeList.size();
        return nodeList.get(index);
    }
}
