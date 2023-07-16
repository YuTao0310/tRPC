package com.trpc.transport.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.trpc.dto.RpcRequest;
import com.trpc.dto.RpcResponse;
import com.trpc.exception.RpcException;
import com.trpc.register.ServiceDiscovery;
import com.trpc.register.zk.ZkServiceDiscoveryImpl;
import com.trpc.serialize.Serializer;
import com.trpc.serialize.hessian.HessianSerializer;
import com.trpc.transport.RpcClientTransport;
import com.trpc.utils.singleton.SingletonFactory;



public class SocketRpcClient implements RpcClientTransport{
    private final ServiceDiscovery serviceDiscovery;
    private final Serializer serializer;
    
    public SocketRpcClient() {
        serviceDiscovery  = SingletonFactory.getInstance(ZkServiceDiscoveryImpl.class);
        serializer = SingletonFactory.getInstance(HessianSerializer.class);
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // Send data to the server through the output stream
            // use user-defined serializer
            byte[] bs = serializer.serialize(rpcRequest);
            objectOutputStream.writeInt(bs.length);
            objectOutputStream.write(bs);
            objectOutputStream.flush();
            // objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // Read RpcResponse from the input stream
            bs = new byte[objectInputStream.readInt()];
            objectInputStream.read(bs);
            return serializer.deserialize(bs, RpcResponse.class);
            //return objectInputStream.readObject();
        } catch (Exception e) {
            throw new RpcException("调用服务失败:", e);
        }
    }

    @Override
    public void close() {
        
    }
    
}
