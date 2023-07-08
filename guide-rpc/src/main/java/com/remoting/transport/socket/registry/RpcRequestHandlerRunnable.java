package com.remoting.transport.socket.registry;

import com.common.message.RpcRequest;
import com.remoting.transport.handlers.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class RpcRequestHandlerRunnable implements Runnable {
    private Socket socket;
    private RpcRequestHandler rpcRequestHandler;
    private ServiceRegistry serviceRegistry;

    public RpcRequestHandlerRunnable(Socket socket, RpcRequestHandler rpcRequestHandler, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.rpcRequestHandler = rpcRequestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try (ObjectInputStream oips = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oops = new ObjectOutputStream(socket.getOutputStream());) {
            //反序列化 request
            RpcRequest rpcRequest = (RpcRequest) oips.readObject();

            //调用函数方法
            String name = rpcRequest.getInterfaceName();

            Object handle = rpcRequestHandler.handle(rpcRequest, serviceRegistry.getService(name));

            //序列化 response
            oops.writeObject(handle);
            oops.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
