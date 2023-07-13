package com.trpc.client;

import com.trpc.config.RpcServiceConfig;
import com.trpc.hello.Hello;
import com.trpc.hello.HelloService;
import com.trpc.proxy.RpcClientProxy;
import com.trpc.proxy.impl.RpcClientCglibProxy;
import com.trpc.proxy.impl.RpcClientJDKProxy;
import com.trpc.transport.RpcClientTransport;
import com.trpc.transport.netty.NettyRpcClient;
import com.trpc.transport.socket.SocketRpcClient;


public class NettyClientMain {
    public static void main(String[] args) {
        RpcClientTransport rpcClientTransport = new NettyRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        RpcClientProxy rpcClientProxy = new RpcClientJDKProxy(rpcClientTransport, rpcServiceConfig);
        // RpcClientProxy rpcClientProxy = new RpcClientCglibProxy(rpcClientTransport, rpcServiceConfig);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        System.out.println(helloService.hello(new Hello("detailed message", "detailed description")));

    }
}
