package com.remoting.transport.socket;

import com.common.message.RpcRequest;
import com.remoting.transport.client.ClientTransport;

import java.io.*;
import java.net.Socket;

public class SocketClient implements ClientTransport {
    private String ip;
    private Integer port;

    public SocketClient(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }
    @Override
    public Object sendMessage(RpcRequest rpcRequest){
        try (Socket socket = new Socket(ip,port);
             ObjectOutputStream oops = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream oips = new ObjectInputStream(socket.getInputStream());
             ) {

            oops.writeObject(rpcRequest);
            oops.flush();
            Object object = oips.readObject();

            return object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
