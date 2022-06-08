package com.wushiyii.core.netty.protocol;

import com.wushiyii.core.model.MyRpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class MyRpcDecoder extends LengthFieldBasedFrameDecoder {

    public MyRpcDecoder() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf msg = (ByteBuf) super.decode(ctx, in);

        byte[] bytes = new byte[msg.readableBytes()];

        msg.readBytes(bytes);

        return new MyRpcProtocol(bytes);
    }
}
