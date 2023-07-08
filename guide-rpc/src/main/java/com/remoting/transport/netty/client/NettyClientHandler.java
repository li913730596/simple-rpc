package com.remoting.transport.netty.client;

import com.common.message.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcResponse) {
                RpcResponse rpcResponse = (RpcResponse) msg;
                Integer code = rpcResponse.getCode();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                // 将服务端的返回结果保存到 AttributeMap 上，AttributeMap 可以看作是一个Channel的共享数据源
                // AttributeMap的key是AttributeKey，value是Attribute
                ctx.channel().attr(key).set(rpcResponse);
                ctx.channel().close();
                if (code.equals(200)) {
                    log.info("{}", rpcResponse.getData());
                } else {
                    log.info("{}", rpcResponse.getMessage());
                }
            }
        } finally {
            //TODO 作用是啥？
            ReferenceCountUtil.release(msg);
        }
    }
}
