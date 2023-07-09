package com.remoting.transport.client;

import com.common.message.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * proxy主要是用来发送rpcRequest
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private ClientTransport rpcClient;
    private AtomicInteger requestId;

    public RpcClientProxy(ClientTransport rpcClient) {
        this.rpcClient = rpcClient;
        requestId = new AtomicInteger(1);
    }

    public <T> T getClient(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        RpcRequest rpcRequest = RpcRequest.builder().interfaceName(method.getDeclaringClass().getName())
                .paramType(method.getParameterTypes())
                .methodName(method.getName())
                .parameters(objects)
                .requestId(String.valueOf(requestId.getAndIncrement()))
                .build();

        return rpcClient.sendMessage(rpcRequest);
    }
}
