package com.trpc.client;

import com.trpc.config.RpcServiceConfig;
import com.trpc.hello.Hello;
import com.trpc.hello.HelloService;
import com.trpc.proxy.RpcClientProxy;
import com.trpc.transport.RpcClientTransport;
import com.trpc.transport.socket.SocketRpcClient;


public class SocketClientMain {
    public static void main(String[] args) {
        RpcClientTransport rpcClientTransport = new SocketRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClientTransport, rpcServiceConfig);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        System.out.println(helloService.hello(new Hello("detailed message", "detailed description")));

    }
}
