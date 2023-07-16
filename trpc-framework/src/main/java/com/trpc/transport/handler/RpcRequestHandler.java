package com.trpc.transport.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.trpc.dto.RpcRequest;
import com.trpc.enums.ServiceProviderEnum;
import com.trpc.exception.RpcException;
import com.trpc.extension.ExtensionLoader;
import com.trpc.provider.ServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getExtension(ServiceProviderEnum.ZK.getName());
    }

    /**
     * Processing rpcRequest: call the corresponding method, and then return the method
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * get method execution results
     *
     * @param rpcRequest client request
     * @param service    service object
     * @return the result of the target method execution
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
