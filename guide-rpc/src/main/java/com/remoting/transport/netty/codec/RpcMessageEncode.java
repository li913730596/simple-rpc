package com.remoting.transport.netty.codec;

import com.constants.RpcConstants;
import com.remoting.transport.netty.message.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.concurrent.atomic.AtomicInteger;

public class RpcMessageEncode extends MessageToByteEncoder<RpcMessage> {

    public static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(RpcConstants.MAGIC_NUMBER);
        byteBuf.readBytes(RpcConstants.VERSION);
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);
        byteBuf.writeByte(rpcMessage.getMessageType());
        byteBuf.writeByte(rpcMessage.getCodec());
        byteBuf.writeByte(rpcMessage.getCompress());  // TODO 压缩方式 是一个枚举类。
        byteBuf.writeInt(ATOMIC_INTEGER.getAndIncrement());

        // 判断是否是心跳包
        if(rpcMessage.getMessageType() != RpcConstants.HEATBEAT_REQUEST_TYPE
            && rpcMessage.getMessageType() != RpcConstants.HEATBEAT_RESPONSE_TYPE){
            //压缩

            //序列化

            //求出  head length + 序列化后的长度 = full length
        }

        //将 fulllength填充


    }
}
