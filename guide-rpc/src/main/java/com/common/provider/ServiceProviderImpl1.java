package com.common.provider;

import com.common.enums.RpcErrorMessageEnum;
import com.common.exceptions.RpcException;
import com.common.provider.ServiceProvider;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderImpl1 implements ServiceProvider {
    private static final Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registedService = ConcurrentHashMap.newKeySet();
    @Override
    public <T> void addServiceProvider(T service, Class<T> serviceClass) {
        String serviceName = service.getClass().getCanonicalName();
        if(serviceMap.get(serviceName) != null){
            return;
        }
        registedService.add(serviceName);

        Class<?>[] interfaces = service.getClass().getInterfaces();

        if(interfaces.length == 0){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?> anInterface : interfaces) {
            serviceMap.put(serviceName,anInterface);
        }
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        if(serviceMap.get(serviceName) == null){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return serviceMap.get(serviceName);
    }
}
