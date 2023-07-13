package com.trpc.loadbalance.loadbalancer;

import java.util.List;
import java.util.Random;

import com.trpc.dto.RpcRequest;
import com.trpc.loadbalance.AbstractLoadBalance;

/**
 * Implementation of random load balancing strategy
 *
 * @author shuang.kou
 * @createTime 2020年06月21日 07:47:00
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
