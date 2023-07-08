package com.remoting.transport.netty.server;

import com.common.enums.RpcResponseCode;
import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import com.remoting.transport.handlers.RpcRequestHandler;
import com.remoting.transport.socket.registry.DefaultServiceRegistry;
import com.remoting.transport.socket.registry.ServiceRegistry;
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
}
