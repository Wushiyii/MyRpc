package com.wushiyii.core.netty;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wushiyii.core.model.NodeInfo;
import com.wushiyii.core.model.RpcRequest;
import com.wushiyii.core.model.RpcResponse;
import com.wushiyii.core.netty.handler.ClientProtocolHandler;
import com.wushiyii.core.netty.protocol.MyRpcDecoder;
import com.wushiyii.core.netty.protocol.MyRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {

    private final NodeInfo nodeInfo;
    private final ClientProtocolHandler clientProtocolHandler;
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

    public NettyClient(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(4,
                new ThreadFactoryBuilder().setNameFormat("MyRpc-Client-Worker-%d").build());
        this.clientProtocolHandler = new ClientProtocolHandler(nodeInfo);
        this.start();
    }

    @SneakyThrows
    public void start() {
        log.info("open connect netty client, nodeInfo={}", nodeInfo);

        CompletableFuture<Boolean> startSuccessFuture = new CompletableFuture<>();

        //异步启动，完成后刷入map中
        new Thread(() -> startSync(startSuccessFuture)).start();


        if (!startSuccessFuture.get(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("connect to ip:" + nodeInfo.getNodeIp() + " port:" + nodeInfo.getNodePort() + " timeout, can not open connection");
        }

    }

    public void startSync(CompletableFuture<Boolean> startSuccessFuture) {

        EventLoopGroup loopGroup = new NioEventLoopGroup(4);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(defaultEventExecutorGroup,
                                new MyRpcDecoder(),
                                new MyRpcEncoder(),
                                new IdleStateHandler(0, 0, 120),
                                clientProtocolHandler)
                        ;
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect(nodeInfo.getNodeIp(), nodeInfo.getNodePort());
        channelFuture.addListener((ChannelFutureListener) future -> {
            log.info("start netty client success");
            startSuccessFuture.complete(Boolean.TRUE);
        });

    }


    public RpcResponse invokeSync(RpcRequest request) {
        return clientProtocolHandler.invokeSync(request);
    }
}
