package com.common.provider;

import com.common.enums.RpcErrorMessageEnum;
import com.common.exceptions.RpcException;
import com.remoting.transport.netty.client.NettyClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChannelProvider {
    private String ip;
    private Integer port;

    private static Bootstrap bootstrap = NettyClient.initializeBootstrap();

    private static final int MAX_ENTRY_COUNT = 5;

    private static Channel channel = null;


//    public ChannelProvider(String ip, Integer port) {
//        this.ip = ip;
//        this.port = port;
//    }

    public static Channel getChannel(InetSocketAddress inetSocketAddress) {
        try {
            //使一个线程在等待另外一些线程完成各自工作之后，再继续执行
            CountDownLatch countDownLatch = new CountDownLatch(1);
            connect(bootstrap, inetSocketAddress, countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("occur execption when get channel:", e);
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress,
                                CountDownLatch countDownLatch) {

        connect(bootstrap, inetSocketAddress, MAX_ENTRY_COUNT, countDownLatch);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, int retry,
                                CountDownLatch countDownLatch) {

        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接成功！");
                channel = future.channel();
                countDownLatch.countDown();
                return;
            }

            if (retry == 0) {
                log.error("客户端连接失败:重试次数已用完，放弃连接！");
                countDownLatch.countDown();
                throw new RpcException(RpcErrorMessageEnum.CLIENT_CONNECT_FAILURE);
            }
            //第几次重试
            int order = (MAX_ENTRY_COUNT - retry) + 1;
            //时间间隔
            int delay = 1 << order;
            log.error("{}: 连接失败，第 {} 次重连……", new Date(), order);

            bootstrap.config().group().schedule(() ->
                            connect(bootstrap, inetSocketAddress, retry - 1, countDownLatch),
                    delay,
                    TimeUnit.SECONDS);

        });
    }
}
