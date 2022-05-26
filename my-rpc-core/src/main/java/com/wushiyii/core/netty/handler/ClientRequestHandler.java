package com.wushiyii.core.netty.handler;


import com.wushiyii.core.model.NodeInfo;
import com.wushiyii.core.model.RpcRequest;
import com.wushiyii.core.model.RpcResponse;
import com.wushiyii.core.netty.NettyClient;

public class ClientRequestHandler {

    public NettyClient nettyClient;

    public ClientRequestHandler(NodeInfo nodeInfo) {
        this.nettyClient = new NettyClient(nodeInfo);
        nettyClient.start();
    }


    public RpcResponse handle(RpcRequest request) {
        return null;
    }
}
