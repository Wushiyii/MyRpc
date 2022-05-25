package com.wushiyii.core.netty.handler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wushiyii.core.model.RpcConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerProtocolHandler extends ChannelInboundHandlerAdapter {

    private final RpcConfig rpcConfig;

    public ServerProtocolHandler(RpcConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
    }


    private static final ExecutorService pool =
            new ThreadPoolExecutor(
                    4,
                    8,
                    60,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(10000),
                    new ThreadFactoryBuilder().setNameFormat("MyRpcServer-%d").build()
            );


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        pool.submit(new ServerProtocolRunnable(ctx, msg, rpcConfig));
    }
}
