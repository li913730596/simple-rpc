package com.remoting.transport.socket;

import com.common.RpcRequest;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class WorkerThread implements Runnable {

    private Socket socket;
    private Object service;

    public WorkerThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {

        try (InputStream ips = socket.getInputStream();
             OutputStream ops = socket.getOutputStream();
             ObjectInputStream oips = new ObjectInputStream(ips);
             ObjectOutputStream oops = new ObjectOutputStream(ops)) {

            RpcRequest request = (RpcRequest) oips.readObject();
            Method method = service.getClass().getMethod(request.getMethodName(),request.getParamType());

            Object o = method.invoke(service, request.getParameters());

            oops.writeObject(o);
            oops.flush();

        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
