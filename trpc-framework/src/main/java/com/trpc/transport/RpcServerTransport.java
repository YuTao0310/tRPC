package com.trpc.transport;

import com.trpc.config.RpcServiceConfig;

public interface RpcServerTransport {
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 9990;
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
