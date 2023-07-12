package com.remoting.transport.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer<T> {

    private static ExecutorService threadPool;

    public SocketServer() {
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(10, 100, 1, TimeUnit.SECONDS, queue, threadFactory);
    }

    public void register(T service, int port, Class<T> serviceClass) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = null;
            while ((socket = serverSocket.accept()) != null) {
                System.out.println("连接建立 ..");
                threadPool.execute(new WorkThread<>(socket, service, serviceClass));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
