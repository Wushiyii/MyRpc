package com.wushiyii.core.netty.protocol;

import com.wushiyii.core.model.MyRpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyRpcEncoder extends MessageToByteEncoder<MyRpcProtocol> {


    /**
     *  length + content (4字节标识content报文长度)
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MyRpcProtocol myRpcProtocol, ByteBuf byteBuf) throws Exception {

        byteBuf.writeShort(myRpcProtocol.getContent().length);
        byteBuf.writeBytes(myRpcProtocol.getContent());
    }



}
