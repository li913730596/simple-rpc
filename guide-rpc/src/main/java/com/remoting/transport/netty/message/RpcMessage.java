package com.remoting.transport.netty.message;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class RpcMessage {
    private Byte messageType;
    private Byte codec;
    private Byte compress;
    private Integer requestId;
    private Object data;
}
