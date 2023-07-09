package com.common.serializer.kryo;


import com.common.message.RpcRequest;
import com.common.message.RpcResponse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.common.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j

public class KryoSerialize implements Serializer {
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true);  //关闭循环引用;默认true;  设置为true会提高效率
        kryo.setRegistrationRequired(false); //是否需要注册服务，false；设置为true对于未注册的类会抛出异常。
        return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream ops = new ByteArrayOutputStream();
        Output output = new Output(ops);){
            Kryo kryo = kryoThreadLocal.get();
            kryoThreadLocal.remove();

            kryo.writeObject(output,obj);
            byte[] bytes = output.toBytes();
            return bytes;

        } catch (IOException e) {
            log.error("kryo序列化出错",e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] obj, Class<T> clazz) {
        try(ByteArrayInputStream ips = new ByteArrayInputStream(obj);
        Input input = new Input(ips);){

            Kryo kryo = kryoThreadLocal.get();
            kryoThreadLocal.remove();

            T object = kryo.readObject(input, clazz);
            return object;

        } catch (IOException e) {
            log.error("kryo反序列化出错",e);
            throw new RuntimeException(e);
        }

    }
}
