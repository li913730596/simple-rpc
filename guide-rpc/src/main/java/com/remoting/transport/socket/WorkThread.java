package com.remoting.transport.socket;

import com.common.message.RpcRequest;
import com.remoting.transport.netty.handlers.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j

public class WorkThread<T> implements Runnable{
    private Socket socket;
    private T service;
    private Class<T> serviceClass;

    private static final RpcRequestHandler rpcRequestHandler;

    static {
        rpcRequestHandler = new RpcRequestHandler();
    }

    public WorkThread(Socket socket, T service, Class<T> serviceClass) {
        this.socket = socket;
        this.service = service;
        this.serviceClass = serviceClass;
    }

    @Override
    public void run() {
        try(ObjectInputStream oips = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oops = new ObjectOutputStream(socket.getOutputStream());) {

            RpcRequest rpcRequest = (RpcRequest) oips.readObject();

            RpcRequestHandler.serviceRegister.addServiceProvider(service,serviceClass);
//            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamType());
//            Object invoke = method.invoke(service, rpcRequest.getParameters());
            Object handle = rpcRequestHandler.handle(rpcRequest);

            oops.writeObject(handle);
            oops.flush();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
