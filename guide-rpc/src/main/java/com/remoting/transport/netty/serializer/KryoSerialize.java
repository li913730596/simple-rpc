package com.remoting.transport.netty.serializer;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.remoting.transport.netty.message.RpcRequest;
import com.remoting.transport.netty.message.RpcResponse;

import java.io.*;

public class KryoSerialize implements Serializer {
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });
    @Override
    public byte[] encode(Object obj) {
        try(ByteArrayOutputStream ops = new ByteArrayOutputStream();
        ObjectOutputStream oops = new ObjectOutputStream(ops);){
            Kryo kryo = kryoThreadLocal.get();
            kryoThreadLocal.remove();
            Output output = new Output();
            kryo.writeObject(output,obj);
            byte[] bytes = output.toBytes();
            return bytes;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T decode(byte[] obj, Class<T> clazz) {

        try(ByteArrayInputStream ips = new ByteArrayInputStream(obj);
        ObjectInputStream inputStream = new ObjectInputStream(ips);){

            Kryo kryo = kryoThreadLocal.get();
            kryoThreadLocal.remove();
            Input input = new Input();
            T object = kryo.readObject(input, clazz);

            return object;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
