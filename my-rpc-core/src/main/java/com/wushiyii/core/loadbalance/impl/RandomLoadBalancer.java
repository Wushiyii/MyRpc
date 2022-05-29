package com.wushiyii.core.loadbalance.impl;

import com.wushiyii.core.loadbalance.LoadBalanceType;
import com.wushiyii.core.loadbalance.LoadBalancer;
import com.wushiyii.core.model.NodeInfo;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {

    private final Random random = new Random();


    @Override
    public String type() {
        return LoadBalanceType.RANDOM;
    }

    @Override
    public NodeInfo select(List<NodeInfo> nodeList) {

        int index = random.nextInt(1024) % nodeList.size();
        return nodeList.get(index);
    }
}
