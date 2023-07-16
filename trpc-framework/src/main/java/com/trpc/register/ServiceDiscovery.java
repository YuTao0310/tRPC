package com.trpc.register;

import java.net.InetSocketAddress;

import com.trpc.dto.RpcRequest;
import com.trpc.extension.SPI;

/**
 * service discovery
 */
@SPI
public interface ServiceDiscovery {
    /**
     * lookup service by rpcServiceName
     *
     * @param rpcRequest rpc service pojo
     * @return service address
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}