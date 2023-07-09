package com.common.message;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Class<?>[] paramType;
    private Object[] parameters;
}
