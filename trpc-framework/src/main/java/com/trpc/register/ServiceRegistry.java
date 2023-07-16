package com.trpc.register;

import java.net.InetSocketAddress;

import com.trpc.extension.SPI;

/**
 * service registration
 */
@SPI
public interface ServiceRegistry {
    /**
     * register service
     *
     * @param rpcServiceName    rpc service name
     * @param inetSocketAddress service address
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}
