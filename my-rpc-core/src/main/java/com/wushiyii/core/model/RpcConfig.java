package com.wushiyii.core.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "MyRpc")
@Data
public class RpcConfig {

    private String registryAddress;

    private Integer rpcPort;

    private String serialize = C.DEFAULT_SERIALIZE;

    private String loadBalance = C.DEFAULT_LOAD_BALANCE;

    private Double weight = C.DEFAULT_WEIGHT;

}
