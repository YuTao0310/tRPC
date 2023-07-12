package com.trpc.register.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import com.trpc.dto.RpcRequest;
import com.trpc.enums.RpcErrorMessageEnum;
import com.trpc.exception.RpcException;
import com.trpc.register.ServiceDiscovery;
import com.trpc.utils.CollectionUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * service discovery based on zookeeper
 */
@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        String targetServiceUrl = serviceUrlList.get(0);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
