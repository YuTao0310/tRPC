package com.trpc.provider.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.trpc.config.RpcServiceConfig;
import com.trpc.enums.RpcErrorMessageEnum;
import com.trpc.exception.RpcException;
import com.trpc.provider.ServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleServiceProviderImpl implements ServiceProvider{
    private final Map<String, Object> serviceMap;
    private final Set<String> registeredService;

    public SimpleServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("Add service: {} and interfaces:{}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }
    
}
