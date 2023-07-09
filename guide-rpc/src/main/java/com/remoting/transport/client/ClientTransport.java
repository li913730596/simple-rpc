package com.remoting.transport.client;

import com.common.message.RpcRequest;

public interface ClientTransport {
    Object sendMessage(RpcRequest rpcRequest);
}
