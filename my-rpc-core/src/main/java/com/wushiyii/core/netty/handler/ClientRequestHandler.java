package com.wushiyii.core.netty.handler;


import com.wushiyii.core.model.RpcRequest;
import com.wushiyii.core.model.RpcResponse;

public interface ClientRequestHandler {

    RpcResponse handle(RpcRequest request);

}
