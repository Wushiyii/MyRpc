package com.wushiyii.core.model;

import lombok.Data;

@Data
public class RpcRequest {

    private String commandId;

    private String providerName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

    private Class<?> returnType;
}
