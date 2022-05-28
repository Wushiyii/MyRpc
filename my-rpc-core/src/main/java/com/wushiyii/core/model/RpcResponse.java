package com.wushiyii.core.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {

    private String commandId;

    private Object responseData;

    private Exception ex;
}
