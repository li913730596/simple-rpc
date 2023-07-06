package com.remoting.transport.netty.server;

import com.remoting.transport.netty.client.RpcClient;
import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jdk.nashorn.internal.runtime.linker.Bootstrap;

import java.nio.charset.Charset;

public class RpcServer {
    private static final ServerBootstrap bootstrap;
    private static final NioEventLoopGroup boss = new NioEventLoopGroup();
    private static final NioEventLoopGroup worker = new NioEventLoopGroup();

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer();
        rpcServer.run(8080);
    }

    static {
        bootstrap = new ServerBootstrap()
                .group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        sc.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                System.out.println("收到的消息为： "  + buf.toString(Charset.defaultCharset()));
                                super.channelRead(ctx, msg);
                            }
                        });

                    }
                });
    }

    public void run(int port){
        try {
            ChannelFuture bind = bootstrap.bind(port);
            Channel channel = bind.sync().channel();
            ChannelFuture channelFuture = channel.closeFuture();
            channelFuture.addListener(future -> {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
