package com.trpc.loadbalance;

import java.util.List;

import com.trpc.dto.RpcRequest;

/**
 * Interface to the load balancing policy
 */
public interface LoadBalance {
    /**
     * Choose one from the list of existing service addresses list
     *
     * @param serviceUrlList Service address list
     * @param rpcRequest
     * @return target service address
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
