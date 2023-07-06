package com.remoting.transport.netty.message;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = -6849794470754667712L;

    private String requestId;

    private Integer code;

    private String message;
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setData(data);
        rpcResponse.setRequestId(requestId);
        return rpcResponse;
    }
    public static <T> RpcResponse<T> fail(String message){ //TODO 待完善
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setMessage("error");
        return rpcResponse;
    }


}
