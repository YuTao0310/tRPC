package com.trpc;
import org.junit.jupiter.api.Test;

import com.trpc.config.RpcServiceConfig;

public class RpcServiceConfigTest {
    
    @Test
    public void lombokSetTest() {
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
        rpcServiceConfig.setPort(9990);
        System.out.println(rpcServiceConfig.getPort());
        rpcServiceConfig.setPort(9991);
        System.out.println(rpcServiceConfig.getPort());
    }
}
