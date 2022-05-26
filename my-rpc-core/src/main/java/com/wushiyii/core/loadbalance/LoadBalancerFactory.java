package com.wushiyii.core.loadbalance;


import com.wushiyii.core.loadbalance.impl.RandomLoadBalancer;
import com.wushiyii.core.loadbalance.impl.RoundRobinLoadBalancer;
import com.wushiyii.core.loadbalance.impl.WeightRoundRobinLoadBalancer;

public class LoadBalancerFactory {

    public static LoadBalancer getLoadBalancerByName(String serializerName) {

        switch (serializerName) {
            case LoadBalanceType.ROUND_ROBIN:
                return new RoundRobinLoadBalancer();
            case LoadBalanceType.WEIGHT_ROUND_ROBIN:
                return new WeightRoundRobinLoadBalancer();
            case LoadBalanceType.RANDOM:
            default:
                return new RandomLoadBalancer();
        }

    }

}
