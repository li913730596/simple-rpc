package com.remoting.transport.codec;

import com.constants.RpcConstants;
import com.remoting.transport.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class RpcMessageDecoder extends ByteToMessageDecoder {
    private Class<?> clazz;
    private Serializer serializer;

    private static final int BODY_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() >= BODY_LENGTH) {

            byteBuf.markReaderIndex();

            int length = byteBuf.readInt();
            System.out.println(length);
            if (length == 0 || byteBuf.readableBytes() < 0) {
                log.info("长度·不对");
                return;
            }

            if(byteBuf.readableBytes() < length){
                System.out.println(byteBuf.readableBytes());
                byteBuf.resetReaderIndex();
                log.info("消息不全");
                return;
            }

            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes);
            Object obj = serializer.deserialize(bytes, clazz);

            list.add(obj);

        }
    }
}
