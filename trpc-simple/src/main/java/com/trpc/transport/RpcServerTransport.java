package com.trpc.transport;

import com.trpc.config.RpcServiceConfig;

public interface RpcServerTransport {
    /**
     * register service in server
     * @param rpcServiceConfig service information
     */
    public void registerService(RpcServiceConfig rpcServiceConfig);
    /**
     * server starts
     */
    public void start();
}
