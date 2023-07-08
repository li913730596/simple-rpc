package com.remoting.transport.netty.server;

import com.common.enums.RpcResponseCode;
import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import com.remoting.transport.netty.handlers.RpcRequestHandler;
import com.common.registry.DefaultServiceRegistry;
import com.common.registry.ServiceRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static ServiceRegistry serviceRegistry;
    private static RpcRequestHandler rpcRequestHandler;

    static {
        serviceRegistry = new DefaultServiceRegistry();
        rpcRequestHandler = new RpcRequestHandler();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof RpcRequest){
            RpcRequest rpcRequest = (RpcRequest) msg;
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object backObj = rpcRequestHandler.handle(rpcRequest, service);

            if(backObj == null){
                RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCode.FAIL);
                ctx.writeAndFlush(rpcResponse);
            }else{
                RpcResponse<Object> rpcResponse = RpcResponse.success(backObj);
                ctx.writeAndFlush(rpcResponse);
            }
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch execption :", cause);
        ctx.newProgressivePromise();
        ctx.close();
    }
}
