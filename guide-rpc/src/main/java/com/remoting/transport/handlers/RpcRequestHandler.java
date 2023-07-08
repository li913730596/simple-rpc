package com.remoting.transport.handlers;

import com.common.enums.RpcErrorMessageEnum;
import com.common.enums.RpcResponseCode;
import com.common.exceptions.RpcException;
import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {
    public Object handle(RpcRequest rpcRequest, Object service){
        log.info("{}",service);
        try {
            Object returnValue =  invokeTargetMethod(rpcRequest, service);
//            return RpcResponse.success(returnValue);
            return returnValue;

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        log.info("{}",service.getClass());
        log.info("{}",service.getClass().getName());
        log.info("{}",service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamType()));
        Class<?> aClass = Class.forName(service.getClass().getName());
        Method method = aClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParamType());
        if (method == null){
            return RpcResponse.fail(RpcResponseCode.FAIL);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
