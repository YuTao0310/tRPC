package com.trpc.register.zk;

import org.apache.curator.framework.CuratorFramework;
import com.trpc.register.ServiceRegistry;
import java.net.InetSocketAddress;

/**
 * service registration  based on zookeeper
 */
public class ZkServiceRegistryImpl implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
