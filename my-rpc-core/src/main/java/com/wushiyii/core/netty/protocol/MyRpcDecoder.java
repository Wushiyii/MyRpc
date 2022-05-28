package com.wushiyii.core.netty.protocol;

import com.wushiyii.core.model.C;
import com.wushiyii.core.model.MyRpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyRpcDecoder extends ByteToMessageDecoder {

    /**
     * 协议开始的标志 magic + type + length 占据6个字节
     */
    public final int BASE_LENGTH = 6;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < BASE_LENGTH) {
            return;
        }

        int beginIndex;
        while (true) {

            // 记录包头开始的index
            beginIndex = byteBuf.readerIndex();

            // 标记包头开始的index
            byteBuf.markReaderIndex();

            // 读到了协议头魔数，结束循环
            if (byteBuf.readByte() == C.PROTOCOL_MAGIC_CODE) {
                break;
            }

            // 未读到包头，略过一个字节
            // 每次略过一个字节，去读取包头信息的开始标记
            byteBuf.resetReaderIndex();
            byteBuf.readByte();

            if (byteBuf.readableBytes() < BASE_LENGTH) {
                return;
            }
        }

        // 读取消息类型
        byte protocolType = byteBuf.readByte();

        // 读取消息长度
        int contentLength = byteBuf.readInt();

        if (byteBuf.readableBytes() < contentLength) {
            // 还原读指针
            byteBuf.readerIndex(beginIndex);
            return;
        }

        byte[] content = new byte[contentLength];
        byteBuf.readBytes(content);

        MyRpcProtocol protocol = new MyRpcProtocol(protocolType, content);
        list.add(protocol);
    }
}
