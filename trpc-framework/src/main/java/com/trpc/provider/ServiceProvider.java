package com.trpc.provider;

import com.trpc.config.RpcServiceConfig;
import com.trpc.extension.SPI;

@SPI
public interface ServiceProvider {

    /**
     * @param rpcServiceName rpc service name
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void publishService(RpcServiceConfig rpcServiceConfig);

}
