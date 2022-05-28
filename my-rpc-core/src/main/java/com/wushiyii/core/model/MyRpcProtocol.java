package com.wushiyii.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyRpcProtocol {

    /**
     * 协议请求类型
     */
    private byte protocolType;

    /**
     * 协议报文
     */
    private byte[] content;

}
