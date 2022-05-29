package com.wushiyii.core.loadbalance;


import com.wushiyii.core.loadbalance.impl.RandomLoadBalancer;
import com.wushiyii.core.loadbalance.impl.RoundRobinLoadBalancer;
import com.wushiyii.core.loadbalance.impl.WeightRoundRobinLoadBalancer;

import java.util.Objects;
import java.util.ServiceLoader;

public class LoadBalancerFactory {

    public static LoadBalancer getLoadBalancerByName(String serializerName) {
        LoadBalancer loadBalancer = getLoadBalancerByNameInterval(serializerName);
        if (Objects.nonNull(loadBalancer)) {
            return loadBalancer;
        }

        //SPI
        ServiceLoader<LoadBalancer> loadBalancers = ServiceLoader.load(LoadBalancer.class);
        for (LoadBalancer balancer : loadBalancers) {
            if (Objects.equals(balancer.type(), serializerName)) {
                return balancer;
            }
        }

        return new RandomLoadBalancer();
    }

    private static LoadBalancer getLoadBalancerByNameInterval(String serializerName) {
        switch (serializerName) {
            case LoadBalanceType.ROUND_ROBIN:
                return new RoundRobinLoadBalancer();
            case LoadBalanceType.WEIGHT_ROUND_ROBIN:
                return new WeightRoundRobinLoadBalancer();
            case LoadBalanceType.RANDOM:
                return new RandomLoadBalancer();
        }
        return null;
    }

}
