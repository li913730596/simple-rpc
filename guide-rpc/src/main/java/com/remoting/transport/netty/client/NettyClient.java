package com.remoting.transport.netty.client;

import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import com.common.serializer.kryo.KryoSerialize;
import com.remoting.transport.codec.RpcMessageDecoder;
import com.remoting.transport.codec.RpcMessageEncoder;
import com.remoting.transport.netty.handlers.RpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient {
    private static final Bootstrap b;
    private static final EventLoopGroup group;

    static {
        KryoSerialize kryoSerialize = new KryoSerialize();
        RpcMessageDecoder responseDecoder = new RpcMessageDecoder(RpcResponse.class, kryoSerialize);
        RpcMessageEncoder requestEncoder = new RpcMessageEncoder(RpcRequest.class, kryoSerialize);
        NettyClientHandler rpcClientHandler = new NettyClientHandler();
        group = new NioEventLoopGroup();
        b = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                //超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                //开启心跳机制
                .option(ChannelOption.SO_KEEPALIVE,true)
                //开启Nagle算法。该算法的作用是尽可能的发送大数据快，减少网络传输。
                // TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(requestEncoder);
                        socketChannel.pipeline().addLast(responseDecoder);
                        socketChannel.pipeline().addLast(rpcClientHandler);
                    }
                });
    }

    public static Bootstrap initializeBootstrap(){
        return b;
    }

    public static void close(){
        log.info("call close method");
        group.shutdownGracefully();
    }

}
