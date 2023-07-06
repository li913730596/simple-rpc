package com.common.message;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    private String interfaceName;
    private String methodName;
    private Class<?>[] paramType;
    private Object[] parameters;
}
