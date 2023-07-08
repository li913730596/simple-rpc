package com.common.serializer;

public interface Serializer {

    byte[] serialize(Object obj);

    <T> T deserialize(byte[] obj, Class<T> clazz);
}
