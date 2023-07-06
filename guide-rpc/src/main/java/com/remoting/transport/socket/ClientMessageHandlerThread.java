package com.remoting.transport.socket;

import com.common.enums.RpcResponseCode;
import com.common.exceptions.RpcException;
import com.common.message.RpcRequest;
import com.common.message.RpcResponse;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class ClientMessageHandlerThread implements Runnable {

    private Socket socket;
    private Object service;

    public ClientMessageHandlerThread(Socket socket, Object service) {
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

//            Method method = service.getClass().getMethod(request.getMethodName(),request.getParamType());
//            Object o = method.invoke(service, request.getParameters());
            Object o = invokeMethod(request);

            oops.writeObject(RpcResponse.success(o));
            oops.flush();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //反射执行方法
    public Object invokeMethod(RpcRequest rpcRequest){
        try {
            String interfaceName = rpcRequest.getInterfaceName();
            Class<?> aClass = Class.forName(interfaceName);

            if(!aClass.isAssignableFrom(service.getClass())){
                return RpcResponse.fail(RpcResponseCode.NOT_FOUND_CLASS);
            }

            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamType());
            if(method == null){
                return RpcResponse.fail(RpcResponseCode.NOT_FOUND_METHOD);
            }

            Object invoke = method.invoke(service, rpcRequest.getParameters());
            return invoke;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
