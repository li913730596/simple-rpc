package com.common.provider;

import com.common.enums.RpcErrorMessageEnum;
import com.common.exceptions.RpcException;
import com.common.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {
    private static final Map<String , Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet(); // map中的key , 与map中key的更新是同步的

    @Override
    //将对象的所有实现类全部注册  TODO 实现成注解
    public <T> void addServiceProvider(T service){
        if(service == null){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }

        String canonicalName = service.getClass().getCanonicalName();
        if(registeredService.contains(canonicalName)){  //已经注册过了
            return;
        }

        registeredService.add(canonicalName);
        Class<?>[] interfaces = service.getClass().getInterfaces();

        if (interfaces.length == 0){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }

        for (Class<?> anInterface : interfaces) {
            String name = anInterface.getName();
            serviceMap.put(name, service);
        }
        log.info("add service {}  and all interfaces  {}",canonicalName, interfaces);
    }

    @Override
    public Object getServiceProvider(String name) {
        Object service = serviceMap.get(name);

        if(service == null){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }


}
