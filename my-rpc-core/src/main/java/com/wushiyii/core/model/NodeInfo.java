package com.wushiyii.core.model;

import lombok.Data;
import lombok.SneakyThrows;

import java.net.InetAddress;

@Data
public class NodeInfo {

    private String serviceName;

    private String nodeIp;

    private Integer nodePort;

    private String serialize;

    private String loadBalance;

    private Double weight;

    @SneakyThrows
    public NodeInfo(ProviderInfo providerInfo, RpcConfig rpcConfig) {
        this.serviceName = providerInfo.getProviderName();
        this.nodeIp = InetAddress.getLocalHost().getHostAddress();
        this.nodePort = rpcConfig.getRpcPort();
        this.serialize = rpcConfig.getSerialize();
        this.loadBalance = rpcConfig.getLoadBalance();
        this.weight = rpcConfig.getWeight();
    }

}
