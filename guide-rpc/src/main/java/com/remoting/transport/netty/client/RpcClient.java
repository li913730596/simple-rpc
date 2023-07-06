package com.remoting.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class RpcClient {

    private int port;
    private String ip;

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient(8080,"127.0.0.1");
        rpcClient.start();
    }

    public RpcClient(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public void start(){
        try {
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        socketChannel.pipeline().addLast(new StringEncoder());
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(ip, port);

        Channel channel = channelFuture.sync().channel();

        channel.writeAndFlush("111");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
