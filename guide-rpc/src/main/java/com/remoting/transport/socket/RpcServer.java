package com.remoting.transport.socket;

import com.common.enums.RpcErrorMessageEnum;
import com.common.exceptions.RpcException;
import com.remoting.transport.socket.handlers.RpcRequestHandler;
import com.remoting.transport.socket.registry.DefaultServiceRegistry;
import com.remoting.transport.socket.registry.RpcRequestHandlerRunnable;
import com.remoting.transport.socket.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RpcServer {

    private ExecutorService threadPool;
    private AtomicInteger cnt = new AtomicInteger(1);
    private final int CORE_POOL_SIZE = 10;
    private final int MAX_POOL_NUM = 100;
    private final int KEEP_ALIVE_TIME = 1;
    private final int BLOCKING_QUEUE_CAPTICITY = 100;
    private final ServiceRegistry serviceRegistry;
    private final RpcRequestHandler rpcRequestHandler = new RpcRequestHandler();

    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        //线程池的参数设置
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPTICITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_NUM,KEEP_ALIVE_TIME,TimeUnit.SECONDS,queue,threadFactory);
    }

    public void start(Object service, int port){

        if(service == null){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }

        try(ServerSocket server = new ServerSocket(port);){
            log.info("servers start...");

            Socket socket;
            while((socket = server.accept()) != null){
                log.info("client connect ..." + cnt.getAndIncrement());
                threadPool.execute(new RpcRequestHandlerRunnable(socket,rpcRequestHandler,
                        serviceRegistry));
//                threadPool.execute(new ClientMessageHandlerThread(socket,service));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
