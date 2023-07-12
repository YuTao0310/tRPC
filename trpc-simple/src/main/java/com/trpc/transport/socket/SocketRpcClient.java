package com.trpc.transport.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.trpc.dto.RpcRequest;
import com.trpc.exception.RpcException;
import com.trpc.register.ServiceDiscovery;
import com.trpc.register.zk.ZkServiceDiscoveryImpl;
import com.trpc.transport.RpcClientTransport;



public class SocketRpcClient implements RpcClientTransport{
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        serviceDiscovery  = new ZkServiceDiscoveryImpl();
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // Send data to the server through the output stream
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // Read RpcResponse from the input stream
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败:", e);
        }
    }
    
}
