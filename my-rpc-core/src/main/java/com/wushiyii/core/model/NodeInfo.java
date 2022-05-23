package com.wushiyii.core.model;

import lombok.Data;
import lombok.SneakyThrows;

import java.net.InetAddress;

@Data
public class NodeInfo {

    private String methodName;

    private String nodeIp;

    private Integer nodePort;

    private String serialize;

    private String loadBalance;

    private Double weight;

    @SneakyThrows
    public NodeInfo(MethodInfo methodInfo, RpcConfig rpcConfig) {
        this.methodName = methodInfo.getMethodName();
        this.nodeIp = InetAddress.getLocalHost().getHostAddress();
        this.nodePort = rpcConfig.getRpcPort();
        this.serialize = rpcConfig.getSerialize();
        this.loadBalance = rpcConfig.getLoadBalance();
        this.weight = rpcConfig.getWeight();
    }

}
