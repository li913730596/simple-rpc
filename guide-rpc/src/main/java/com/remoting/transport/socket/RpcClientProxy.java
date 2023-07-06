package com.remoting.transport.socket;

import com.common.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private String ip;
    private Integer port;

    public RpcClientProxy(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public <T> T getClinet(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},RpcClientProxy.this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        RpcRequest rpcRequest = RpcRequest.builder().interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramType(method.getParameterTypes())
                .parameters(objects).build();

        RpcClient rpcClient = new RpcClient();
        Object back = rpcClient.sendMessage(rpcRequest, ip, port);

        return back;
    }
}
