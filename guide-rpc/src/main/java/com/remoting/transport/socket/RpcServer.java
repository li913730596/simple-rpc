package com.remoting.transport.socket;

import com.common.enums.RpcErrorMessageEnum;
import com.common.exceptions.RpcException;
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

    public RpcServer() {
        //线程池的参数设置
        int corePoolSize = 10;
        int maxmumPoolSize = 100;
        int keepAliveTime = 1;
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool = new ThreadPoolExecutor(corePoolSize,maxmumPoolSize,keepAliveTime,TimeUnit.SECONDS,queue,threadFactory);
    }

    public void register(Object service, int port){

        if(service == null){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_NULL);
        }

        try(ServerSocket server = new ServerSocket(port);){
            log.info("servers start...");

            Socket socket;
            while((socket = server.accept()) != null){
                log.info("client connect ..." + cnt.getAndIncrement());
                threadPool.execute(new ClientMessageHandlerThread(socket,service));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
