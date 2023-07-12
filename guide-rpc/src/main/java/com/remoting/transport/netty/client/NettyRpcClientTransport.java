package com.remoting.transport.netty.client;

import com.common.enums.RpcErrorMessageEnum;
import com.common.exceptions.RpcException;
import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import com.remoting.transport.client.ClientTransport;
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
public class NettyRpcClientTransport implements ClientTransport {

    private Channel channel;

    public NettyRpcClientTransport(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Object sendMessage(RpcRequest rpcRequest) {
        try {
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
                //TODO 检验 request 和 response
                if(rpcResponse.getResponseId().equals(rpcRequest.getRequestId())) {
                    log.info("调用服务成功,serviceName :{}, RpcResponse:{}",
                            rpcRequest.getInterfaceName(),rpcResponse);
                    return rpcResponse.getData();
                }
                log.error("调用服务失败,serviceName :{}, RpcResponse:{}",
                        rpcRequest.getInterfaceName(),rpcResponse);

                throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
