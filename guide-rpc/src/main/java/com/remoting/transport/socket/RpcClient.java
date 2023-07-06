package com.remoting.transport.socket;

import com.common.RpcRequest;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class RpcClient {
    private String ip;
    private Integer port;


    public Object sendMessage(RpcRequest rpcRequest, String ip, Integer port) {
        try (Socket socket = new Socket(ip, port);
             ObjectOutputStream oops = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oips = new ObjectInputStream(socket.getInputStream());
        ) {

            oops.writeObject(rpcRequest);
            Object readObject = oips.readObject();
            return readObject;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
