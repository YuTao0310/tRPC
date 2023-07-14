package com.trpc.transport.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.trpc.dto.RpcRequest;
import com.trpc.dto.RpcResponse;
import com.trpc.serialize.Serializer;
import com.trpc.serialize.hessian.HessianSerializer;
import com.trpc.transport.handler.RpcRequestHandler;
import com.trpc.utils.singleton.SingletonFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketRpcServerHandler implements Runnable {
    private final Socket socket;
    private final RpcRequestHandler rpcRequestHandler;
    private final Serializer serializer;


    public SocketRpcServerHandler(Socket socket) {
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
        this.serializer = SingletonFactory.getInstance(HessianSerializer.class);
    }

    @Override
    public void run() {
        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            byte[] bs = new byte[objectInputStream.readInt()];
            objectInputStream.read(bs);
            RpcRequest rpcRequest = serializer.deserialize(bs, RpcRequest.class);
            // RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = rpcRequestHandler.handle(rpcRequest);
            bs = serializer.serialize(RpcResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.writeInt(bs.length);
            objectOutputStream.write(bs);
            // objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        } catch (Exception e) {
            log.error("occur exception:", e);
        }
    }

}
