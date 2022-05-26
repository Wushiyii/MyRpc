package com.wushiyii.core.netty.handler;


import com.wushiyii.core.model.NodeInfo;
import com.wushiyii.core.model.RpcRequest;
import com.wushiyii.core.model.RpcResponse;
import com.wushiyii.core.netty.NettyClient;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientRequestHandler extends ChannelInboundHandlerAdapter implements ClientRequestHandler{

    public NettyClient nettyClient;

    public NettyClientRequestHandler(NodeInfo nodeInfo) {
        this.nettyClient = new NettyClient(nodeInfo);
        nettyClient.start();
    }



    @Override
    public RpcResponse handle(RpcRequest request) {
        return null;
    }
}
