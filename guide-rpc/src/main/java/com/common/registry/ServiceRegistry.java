package com.common.registry;

import org.apache.curator.shaded.com.google.common.net.InetAddresses;

import java.net.InetSocketAddress;

public interface ServiceRegistry {
    void registerService(String serviceName, InetSocketAddress inetSocketAddress);
    Object lookupService(String serviceName);
}
