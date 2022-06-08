package com.wushiyii.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyRpcProtocol {

    /**
     * 协议报文
     */
    private byte[] content;

}
