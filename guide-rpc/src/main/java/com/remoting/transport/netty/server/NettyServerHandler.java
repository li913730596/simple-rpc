package com.remoting.transport.netty.server;

import com.common.enums.RpcResponseCode;
import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import com.remoting.transport.netty.handlers.RpcRequestHandler;
import com.common.registry.DefaultServiceRegistry;
import com.common.registry.ServiceRegistry;
import com.utils.concurrent.ThreadPoolFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static ServiceRegistry serviceRegistry;
    private static RpcRequestHandler rpcRequestHandler;
    private static ExecutorService threadPool;

    static {
        serviceRegistry = new DefaultServiceRegistry();
        rpcRequestHandler = new RpcRequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool("netty-server-handler-rpc-pool");

    }
    @Override //TODO  关于ByteBuf的释放问题
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       threadPool.execute(() -> {
           log.info("server handle message from client by thread: {}", Thread.currentThread().getName());
                   try {
                       if (msg instanceof RpcRequest) {
                           log.info("server recive msg {}", msg);
                           RpcRequest rpcRequest = (RpcRequest) msg;
                           String interfaceName = rpcRequest.getInterfaceName();
                           Object service = serviceRegistry.getService(interfaceName);
                           Object backObj = rpcRequestHandler.handle(rpcRequest, service);

                           if (backObj == null) {
                               RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCode.FAIL, rpcRequest.getRequestId());
                               ctx.writeAndFlush(rpcResponse);
                           } else {
                               RpcResponse<Object> rpcResponse = RpcResponse.success(backObj, rpcRequest.getRequestId());
                               ctx.writeAndFlush(rpcResponse);
                           }
                       }
                   } finally {
                       //释放ByteBuf
                       ReferenceCountUtil.release(msg);
                   }
               }
       );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch execption :", cause);
        ctx.newProgressivePromise();
        ctx.close();
    }
}
