package com.remoting.transport.netty.handlers;

import com.common.enums.RpcErrorMessageEnum;
import com.common.enums.RpcResponseCode;
import com.common.exceptions.RpcException;
import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import com.common.provider.ServiceProvider;
import com.common.provider.ServiceProviderImpl;
import com.common.registry.ServiceRegistry;
import com.common.registry.ZkServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {

    /**
     * 使用ServiceProvider获取服务对象
     */
    public static final ServiceProvider serviceRegister;
    static {
        serviceRegister = new ServiceProviderImpl();
    }
    public Object handle(RpcRequest rpcRequest){
        Object provider = serviceRegister.getServiceProvider(rpcRequest.getInterfaceName());
        log.info("{}",provider);
        try {
            Object returnValue =  invokeTargetMethod(rpcRequest, provider);
            return returnValue;

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        Class<?> aClass = Class.forName(service.getClass().getName());
        Method method = aClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParamType());
        if (method == null){
            return RpcResponse.fail(RpcResponseCode.FAIL, rpcRequest.getRequestId());
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
