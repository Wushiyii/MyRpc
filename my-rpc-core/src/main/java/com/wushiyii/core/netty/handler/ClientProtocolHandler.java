package com.wushiyii.core.netty.handler;


import com.wushiyii.core.invocation.RpcInvocationHandler;
import com.wushiyii.core.model.*;
import com.wushiyii.core.serialize.SerializeUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientProtocolHandler extends ChannelInboundHandlerAdapter {

    public NodeInfo nodeInfo;

    public ClientProtocolHandler(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    private ChannelHandlerContext ctx;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyRpcProtocol responseMsg = (MyRpcProtocol) msg;
        RpcResponse rpcResponse = SerializeUtil.deserializer(responseMsg.getContent(), RpcResponse.class);
        CompletableFuture<RpcResponse> holdFuture = holdMap.get(rpcResponse.getCommandId());
        if (Objects.nonNull(holdFuture)) {
            holdFuture.complete(rpcResponse);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("channel error, address={}", nodeInfo.toAddress(), cause);
        RpcInvocationHandler.invalidChannel(nodeInfo.toAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("channel inactive , address={}", nodeInfo.toAddress());
        RpcInvocationHandler.invalidChannel(nodeInfo.toAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    private final Map<String, CompletableFuture<RpcResponse>> holdMap = new ConcurrentHashMap<>(128);

    public RpcResponse invokeSync(RpcRequest request) {

        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        try {
            //请求窗口
            holdMap.put(request.getCommandId(), responseFuture);

            //序列化
            byte[] byteRequest = SerializeUtil.serialize(request);
            MyRpcProtocol protocol = new MyRpcProtocol(byteRequest);
            ctx.writeAndFlush(protocol);

            //CompletableFuture阻塞请求
            RpcResponse rpcResponse = responseFuture.get(10, TimeUnit.SECONDS);

            if (Objects.isNull(rpcResponse)) {
                throw new RuntimeException("call rpc timeout, address=" + nodeInfo.toAddress() + ", request=" + protocol);
            }
            return rpcResponse;

        } catch (Exception e) {
            log.error("call rpc occur error", e);
            throw new RuntimeException("call rpc occur error", e);
        } finally {
            holdMap.remove(request.getCommandId());
        }
    }
}
