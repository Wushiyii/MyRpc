package com.wushiyii.core.model;

import lombok.Data;

@Data
public class RpcResponse {

    private String commandId;

    private Object responseData;

    private Exception ex;
}
