package com.remoting.transport.netty.client;

import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import com.remoting.transport.client.RpcClient;
import com.remoting.transport.codec.RpcMessageDecoder;
import com.remoting.transport.codec.RpcMessageEncoder;
import com.common.serializer.kryo.KryoSerialize;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClient implements RpcClient {

    private int port;
    private String ip;

    private static final Bootstrap bootstrap;

    public static void main(String[] args) throws InterruptedException {
//        RpcClient rpcClient = new RpcClient(8080,"127.0.0.1");
//        rpcClient.start();
        Channel channel = NettyRpcClient.bootstrap.connect("127.0.0.1", 8080).sync().channel();
        channel.writeAndFlush("111");
    }

    public NettyRpcClient(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        KryoSerialize kryoSerialize = new KryoSerialize();
        bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        socketChannel.pipeline().addLast(new RpcMessageDecoder(RpcResponse.class,kryoSerialize));
                        socketChannel.pipeline().addLast(new RpcMessageEncoder(RpcRequest.class,kryoSerialize));
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }


    @Override
    public Object sendMessage(RpcRequest rpcRequest) {
        try {
            Channel channel = bootstrap.connect(ip, port).sync().channel();
            if(channel != null) {
                channel.writeAndFlush(rpcRequest);
                ChannelFuture channelFuture = channel.closeFuture();
                channelFuture.addListener(future -> {
                    if (future.isSuccess()) {
                        log.info("{}", future.getNow());
                    } else {
                        log.info("{}", future.cause().getMessage());
                    }
                });

                //同步阻塞
                channelFuture.sync();

                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
