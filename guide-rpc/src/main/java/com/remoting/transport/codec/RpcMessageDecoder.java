package com.remoting.transport.codec;

import com.common.serializer.Serializer;
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
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }

            if(byteBuf.readableBytes() < length){
                byteBuf.resetReaderIndex();
                return;
            }

            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes);
            Object obj = serializer.deserialize(bytes, clazz);

            list.add(obj);
            log.info("success decode Bytebuf to Object");
        }
    }
}
