package com.trpc.config;

import java.net.InetSocketAddress;

import com.trpc.register.zk.CuratorUtils;
import com.trpc.utils.threadpool.ThreadPoolFactoryUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * When the server  is closed, do something such as unregister all services
 */
@Slf4j
public class CustomShutdownHook {

    public static void clearAll(String host, int port) {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
            CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            log.info("cancel registering : " + inetSocketAddress);
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }
}