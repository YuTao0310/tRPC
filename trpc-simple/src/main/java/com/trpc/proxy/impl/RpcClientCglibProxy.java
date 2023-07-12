package com.trpc.proxy.impl;

import java.lang.reflect.Method;
import java.util.UUID;

import com.trpc.config.RpcServiceConfig;
import com.trpc.dto.RpcRequest;
import com.trpc.dto.RpcResponse;
import com.trpc.proxy.ResultChecker;
import com.trpc.proxy.RpcClientProxy;
import com.trpc.transport.RpcClientTransport;
import com.trpc.transport.socket.SocketRpcClient;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Cglib Dynamic Proxy
 * The whole process is similar to RpcClientJDKProxy
 */
@Slf4j
public class RpcClientCglibProxy implements RpcClientProxy ,MethodInterceptor {

    private final RpcClientTransport rpcClientTransport;
    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientCglibProxy(RpcClientTransport rpcClientTransport, RpcServiceConfig rpcServiceConfig) {
        this.rpcClientTransport = rpcClientTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }


    public RpcClientCglibProxy(RpcClientTransport rpcClientTransport) {
        this.rpcClientTransport = rpcClientTransport;
        this.rpcServiceConfig = new RpcServiceConfig();
    }
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(clazz.getClassLoader());
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return (T)enhancer.create();
    }
    @Override
    @SuppressWarnings("unchecked")
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        log.info("Cglib Dynamic Proxy invoked method: " + method.getName());
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
        ResultChecker.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }
}
