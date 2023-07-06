package com.remoting.transport.socket;

import com.common.enums.RpcErrorMessageEnum;
import com.common.enums.RpcResponseCode;
import com.common.exceptions.RpcException;
import com.common.message.RpcRequest;
import com.common.message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class RpcClient {
    private String ip;
    private Integer port;

    public Object sendMessage(RpcRequest rpcRequest, String ip, Integer port) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream oops = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oips = new ObjectInputStream(socket.getInputStream());
        ) {
            //发送rpc请求
            oops.writeObject(rpcRequest);
            //接受rpc响应
            Object readObject = oips.readObject();

            if (readObject == null) {
                throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE,
                        "interfaceName: " + rpcRequest.getInterfaceName());
            }

            if (readObject instanceof RpcResponse) {
                RpcResponse rpcResponse = (RpcResponse) readObject;
                if (rpcResponse.getCode() == null ||
                        !rpcResponse.getCode().equals(RpcResponseCode.SUCCESS.getCode()) ) {
                    throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE,
                            "interfaceName: " + rpcRequest.getInterfaceName());
                }
                return rpcResponse.getData();
            }

            return null;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("服务调用失败",e);
        }
    }
}
