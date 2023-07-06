package com.common;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
public class RpcRequest implements Serializable {

    private String interfaceName;
    private String methodName;
    private Class<?>[] paramType;
    private Object[] parameters;
}
