package com.trpc.server;

import com.trpc.config.RpcServiceConfig;
import com.trpc.hello.HelloService;
import com.trpc.server.serviceImpl.HelloServiceImpl;
import com.trpc.transport.RpcServerTransport;
import com.trpc.transport.socket.SocketRpcServer;

import lombok.extern.slf4j.Slf4j;

public class SocketServerMain {
    public static void main(String[] args) {
         HelloService helloService = new HelloServiceImpl();
        RpcServerTransport rpcServerTransport = new SocketRpcServer();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setService(helloService);
        rpcServerTransport.registerService(rpcServiceConfig);  
        rpcServerTransport.start();
    }
}
