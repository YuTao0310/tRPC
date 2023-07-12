package com.trpc.client;

import java.lang.reflect.Method;
import java.util.UUID;

import com.trpc.config.RpcServiceConfig;
import com.trpc.dto.RpcRequest;
import com.trpc.dto.RpcResponse;
import com.trpc.hello.Hello;
import com.trpc.hello.HelloService;
import com.trpc.transport.RpcClientTransport;
import com.trpc.transport.socket.SocketRpcClient;


public class SocketClientMain {
    public static void main(String[] args) {
        RpcClientTransport rpcClientTransport = new SocketRpcClient();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        RpcRequest rpcRequest = RpcRequest.builder().methodName("hello")
                .parameters(new Object[]{(Object)new Hello("detailed message", "detailed description")})
                .interfaceName(HelloService.class.getName())
                .paramTypes(new Class[]{Hello.class})
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();
        RpcResponse<Object> RpcResponse = (RpcResponse<Object>)rpcClientTransport.sendRpcRequest(rpcRequest);
        System.out.println(RpcResponse.getData());

    }
}
