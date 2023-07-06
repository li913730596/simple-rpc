package com.remoting.transport.netty.serializer;

public interface Serializer {

    byte[] encode(Object obj);

    <T> T decode(byte[] obj, Class<T> clazz);
}
