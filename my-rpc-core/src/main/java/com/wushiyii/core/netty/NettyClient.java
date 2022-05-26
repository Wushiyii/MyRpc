package com.wushiyii.core.netty;


import com.wushiyii.core.model.NodeInfo;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {

    private final NodeInfo nodeInfo;

    public NettyClient(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    @SneakyThrows
    public void start() {
        log.info("open connect netty client, nodeInfo={}", nodeInfo);

        CountDownLatch latch = new CountDownLatch(1);

        //异步启动，完成后刷入map中
        new Thread(() -> startSync(latch)).start();

        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("connect to ip:" + nodeInfo.getNodeIp() + " port:" + nodeInfo.getNodePort() + " timeout, can not open connection");
        }

    }

    public void startSync(CountDownLatch latch) {

        EventLoopGroup loopGroup = new NioEventLoopGroup(4);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new MyRpcDecoder())
                                .addLast(new MyRpcEncoder())
                                .addLast(new ClientProtocolHandler(nodeInfo))
                        ;
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect(nodeInfo.getNodeIp(), nodeInfo.getNodePort());
        channelFuture.addListener((ChannelFutureListener) future -> {
            latch.countDown();
        });

    }
}
