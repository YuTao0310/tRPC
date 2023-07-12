package com.trpc.proxy;

public interface RpcClientProxy {
    public <T> T getProxy(Class<T> clazz);
}
