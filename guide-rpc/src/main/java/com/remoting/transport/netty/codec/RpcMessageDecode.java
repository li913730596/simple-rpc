package com.remoting.transport.netty.codec;

import com.constants.RpcConstants;
import com.remoting.transport.netty.message.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class RpcMessageDecode extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecode(){
        this(RpcConstants.MAX_FRAME_LENGTH,5,4,-9,0);
    }
    public RpcMessageDecode(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if(decode instanceof ByteBuf){
            ByteBuf frame = (ByteBuf) decode;

            if(frame.readableBytes() >= RpcConstants.TOTAL_LENGTH){
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error", e);
                    throw e;
                }finally {
                    frame.release();
                }
            }

        }
        return decode;
    }

    public Object decodeFrame(ByteBuf in){
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codec = in.readByte();
        byte compress = in.readByte();
        int requestId = in.readInt();

        RpcMessage rpcMessage = RpcMessage.builder()
                .messageType(messageType)
                .requestId(requestId)
                .compress(compress)
                .codec(codec).build();

        if(fullLength - RpcConstants.HEAD_LENGTH > 0){
            //解压

            //反序列化

            //setData

        }
        return rpcMessage;
    }

    public void checkVersion(ByteBuf in){
        byte version = RpcConstants.VERSION;
        byte readByte = in.readByte();
        if(readByte != version){
            throw new RuntimeException("Version is not compatible: " + version);
        }
    }

    public void checkMagicNumber(ByteBuf in){
        byte[] bytes = RpcConstants.MAGIC_NUMBER;
        int n = bytes.length;
        byte[] rcv = new byte[n];
        in.readBytes(rcv);

        for(int i = 0; i < n; i ++){
            if(rcv[i] != bytes[i])
                throw new RuntimeException("Unknow magic number:" + Arrays.toString(rcv));
        }

    }

}
