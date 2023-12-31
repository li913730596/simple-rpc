package com.common.provider;

public interface ServiceProvider {
    /**
     * 保存服务提供者
     */
    <T> void addServiceProvider(T service, Class<T> serviceClass);


    /**
     * 获取服务提供者
     */
    Object getServiceProvider(String serviceName);
}
