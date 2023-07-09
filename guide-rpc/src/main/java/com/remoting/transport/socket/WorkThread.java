package com.remoting.transport.socket;

import com.common.message.RpcRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class WorkThread implements Runnable{
    private Socket socket;
    private Object service;

    public WorkThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        try(ObjectInputStream oips = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oops = new ObjectOutputStream(socket.getOutputStream());) {

            RpcRequest rpcRequest = (RpcRequest) oips.readObject();

            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamType());
            Object invoke = method.invoke(service, rpcRequest.getParameters());

            oops.writeObject(invoke);
            oops.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
