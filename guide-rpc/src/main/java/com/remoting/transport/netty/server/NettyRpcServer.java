package com.remoting.transport.netty.server;

import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import com.remoting.transport.codec.RpcMessageDecoder;
import com.remoting.transport.codec.RpcMessageEncoder;
import com.remoting.transport.serializer.kryo.KryoSerialize;
import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jdk.nashorn.internal.runtime.linker.Bootstrap;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
@Slf4j
public class NettyRpcServer {
    private static final ServerBootstrap bootstrap;
    private static final NioEventLoopGroup boss = new NioEventLoopGroup();
    private static final NioEventLoopGroup worker = new NioEventLoopGroup();
    private static final KryoSerialize kryoSerialize = new KryoSerialize();

    public static void main(String[] args) {
        NettyRpcServer rpcServer = new NettyRpcServer();
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
                        sc.pipeline().addLast(new RpcMessageDecoder(RpcRequest.class,kryoSerialize));
                        sc.pipeline().addLast(new RpcMessageEncoder(RpcResponse.class,kryoSerialize));
                        sc.pipeline().addLast(new NettyServerHandler());
                    }
                });
    }

    public void run(int port){
        try {
            ChannelFuture bind = bootstrap.bind(port);
            Channel channel = bind.sync().channel();
            ChannelFuture channelFuture = channel.closeFuture();
            channelFuture.addListener(future -> {
                if(!future.isSuccess()){
                    log.info("{}",future.cause().getMessage());
                }
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
