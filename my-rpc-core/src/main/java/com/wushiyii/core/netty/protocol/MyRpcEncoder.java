package com.wushiyii.core.netty.protocol;

import com.wushiyii.core.model.C;
import com.wushiyii.core.model.MyRpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyRpcEncoder extends MessageToByteEncoder<MyRpcProtocol> {


    /**
     * magic + type + length + content
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MyRpcProtocol myRpcProtocol, ByteBuf byteBuf) throws Exception {

        byteBuf.writeByte(C.PROTOCOL_MAGIC_CODE);
        byteBuf.writeByte(myRpcProtocol.getProtocolType());
        byteBuf.writeInt(myRpcProtocol.getContent().length);
        byteBuf.writeBytes(myRpcProtocol.getContent());
    }



}
