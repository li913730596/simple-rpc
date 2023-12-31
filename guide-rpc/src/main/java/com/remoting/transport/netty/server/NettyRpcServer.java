package com.remoting.transport.netty.server;

import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import com.common.provider.ServiceProvider;
import com.common.provider.ServiceProviderImpl;
import com.common.registry.ServiceRegistry;
import com.common.registry.ZkServiceRegistry;
import com.remoting.transport.codec.RpcMessageDecoder;
import com.remoting.transport.codec.RpcMessageEncoder;
import com.common.serializer.kryo.KryoSerialize;
import com.utils.zk.CuratorHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyRpcServer {
    private static final ServerBootstrap bootstrap;
    private static final NioEventLoopGroup boss = new NioEventLoopGroup();
    private static final NioEventLoopGroup worker = new NioEventLoopGroup();
    private static final KryoSerialize kryoSerialize = new KryoSerialize();
    private final ServiceProvider serviceProvider;
    private final ServiceRegistry serviceRegistry;
    private String host;
    private Integer port;


    public NettyRpcServer(String host, Integer port) {
        this.host = host;
        this.port = port;
        serviceProvider = new ServiceProviderImpl();
        serviceRegistry = new ZkServiceRegistry();
    }

    static {
        bootstrap = new ServerBootstrap()
                .group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，
                // 服务器处理创建新连接较慢，可以适当调大这个参数
                .childOption(ChannelOption.SO_BACKLOG,128)
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

    public <T> void publishService(T service, Class<T> serviceClass){
        //在服务器上保存一份服务实例
        serviceProvider.addServiceProvider(service, serviceClass);
        //在ZookeePeer中存储服务对应的服务器地址，供客户端连接
        serviceRegistry.registerService(serviceClass.getCanonicalName(), new InetSocketAddress(host,port));
        run();
    }

    public void run(){
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
