package com.common.provider;

import com.remoting.transport.netty.client.NettyClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

public class ChannelProvider {
    private String ip;
    private Integer port;

    private final Bootstrap b = NettyClient.initializeBootstrap();

    public ChannelProvider(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public Channel getChannel(){
        try {
            Channel channel = b.connect(ip, port).sync().channel();
            return channel;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
