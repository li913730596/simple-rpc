package com.remoting.transport.client;

import com.common.message.RpcRequest;

public interface RpcClient {
    Object sendMessage(RpcRequest rpcRequest);
}
