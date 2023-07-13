package com.trpc.proxy.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.trpc.config.RpcServiceConfig;
import com.trpc.dto.RpcRequest;
import com.trpc.dto.RpcResponse;
import com.trpc.enums.RpcErrorMessageEnum;
import com.trpc.enums.RpcResponseCodeEnum;
import com.trpc.exception.RpcException;
import com.trpc.proxy.ResultChecker;
import com.trpc.proxy.RpcClientProxy;
import com.trpc.transport.RpcClientTransport;
import com.trpc.transport.netty.NettyRpcClient;
import com.trpc.transport.socket.SocketRpcClient;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Dynamic proxy class.
 * When a dynamic proxy object calls a method, it actually calls the following invoke method.
 * It is precisely because of the dynamic proxy that the remote method called by the client is like calling the local method (the intermediate process is shielded)
 */
@Slf4j
public class RpcClientJDKProxy implements RpcClientProxy, InvocationHandler{
    /**
     * Used to send requests to the server.And there are two implementations: socket and netty
     */
    private final RpcClientTransport rpcClientTransport;
    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientJDKProxy(RpcClientTransport rpcClientTransport, RpcServiceConfig rpcServiceConfig) {
        this.rpcClientTransport = rpcClientTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }


    public RpcClientJDKProxy(RpcClientTransport rpcClientTransport) {
        this.rpcClientTransport = rpcClientTransport;
        this.rpcServiceConfig = new RpcServiceConfig();
    }

   /**
     * get the proxy object
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("JDK Dynamic proxy invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;

        if (rpcClientTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) rpcClientTransport.sendRpcRequest(rpcRequest);
        }

        if (rpcClientTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) rpcClientTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        ResultChecker.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }


}
