package com.remoting.transport.client;

import com.common.message.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * proxy主要是用来发送rpcRequest
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private RpcClient rpcClient;

    public RpcClientProxy(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
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
                .build();

        return rpcClient.sendMessage(rpcRequest);
    }
}
