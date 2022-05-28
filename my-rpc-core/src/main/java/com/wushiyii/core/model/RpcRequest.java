package com.wushiyii.core.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {

    private String commandId;

    private String providerName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}
