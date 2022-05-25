package com.wushiyii.core.netty;


import com.wushiyii.core.model.RpcConfig;
import com.wushiyii.core.netty.handler.ServerProtocolHandler;
import com.wushiyii.core.netty.protocol.MyRpcDecoder;
import com.wushiyii.core.netty.protocol.MyRpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {

    private final RpcConfig rpcConfig;

    public NettyServer(RpcConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
    }


    public void start() {
        log.info("staring netty server, config={}", rpcConfig);
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new MyRpcDecoder())
                                    .addLast(new MyRpcEncoder())
                                    .addLast(new ServerProtocolHandler(rpcConfig));
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(rpcConfig.getRpcPort()).sync();

            log.info("start netty server success");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("start netty server error ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
