package com.wushiyii.core.netty.handler;


import com.wushiyii.core.model.NodeInfo;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientProtocolHandler extends ChannelInboundHandlerAdapter {

    public NodeInfo nodeInfo;

    public ClientProtocolHandler(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }




}
