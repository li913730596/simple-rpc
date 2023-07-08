package com.common.registry;

public interface ServiceRegistry {
    <T> void register(T service);
    Object getService(String name);
}
