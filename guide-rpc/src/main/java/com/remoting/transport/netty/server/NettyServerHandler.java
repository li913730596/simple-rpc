package com.remoting.transport.netty.server;

import com.common.enums.RpcResponseCode;
import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import com.common.provider.ServiceProvider;
import com.remoting.transport.netty.handlers.RpcRequestHandler;
import com.common.provider.ServiceProviderImpl;
import com.utils.concurrent.ThreadPoolFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final RpcRequestHandler rpcRequestHandler;
    private static final ExecutorService threadPool;
    private static final String THREAD_NAME_PREFIX = "netty-server-handler-rpc-pool";

    static {
        rpcRequestHandler = new RpcRequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);

    }
    @Override //TODO  关于ByteBuf的释放问题
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       threadPool.execute(() -> {
           log.info("server handle message from client by thread: {}", Thread.currentThread().getName());
                   try {
                       if (msg instanceof RpcRequest) {
                           log.info("server recive msg {}", msg);
                           RpcRequest rpcRequest = (RpcRequest) msg;
                           Object backObj = rpcRequestHandler.handle(rpcRequest);

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
