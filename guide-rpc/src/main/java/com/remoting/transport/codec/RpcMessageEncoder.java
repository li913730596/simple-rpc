package com.remoting.transport.codec;

import com.common.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class RpcMessageEncoder extends MessageToByteEncoder<Object> {

    private Class<?> clazz;
    private Serializer serializer;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (clazz.isInstance(o)){   // 消息o是clazz类型或者对应子类的实例对象
            byte[] bytes = serializer.serialize(o);
            int length = bytes.length;
            //暂定协议格式： 长度 + 内容
            byteBuf.writeInt(length);
            byteBuf.writeBytes(bytes);
        }
    }
}
