package com.remoting.transport.netty.message;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -6849794470754667711L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private String version;
    private String group;

    public String getRpcServiceName(){
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
