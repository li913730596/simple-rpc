package com.common.message;

import com.common.enums.RpcResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = -6849794470754667710L;
    private Integer code;
    private String message;
    private String responseId;

    private T data;

    public static <T> RpcResponse<T> success(T data, String id) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setCode(RpcResponseCode.SUCCESS.getCode());
        if (data != null) {
            rpcResponse.setData(data);
            rpcResponse.setResponseId(id);
        }
        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail(RpcResponseCode rpcResponseCode, String id) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setCode(rpcResponse.getCode());
        rpcResponse.setMessage(rpcResponse.getMessage());
        rpcResponse.setResponseId(id);
        return rpcResponse;
    }
}
