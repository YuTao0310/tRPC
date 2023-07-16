package com.trpc.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.trpc.config.RpcServiceConfig;
import com.trpc.hello.Hello;
import com.trpc.hello.HelloService;
import com.trpc.proxy.RpcClientProxy;
import com.trpc.proxy.impl.RpcClientJDKProxy;
import com.trpc.transport.RpcClientTransport;
import com.trpc.transport.netty.client.NettyRpcClient;
import com.trpc.utils.threadpool.ThreadPoolFactoryUtil;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientMain {

    private static class ClientRunnable implements Runnable {

        private final int count;

        public ClientRunnable() {
            this(1);
        }

        public ClientRunnable(int count) {
            this.count = count;
        }

        @Override
        public void run() {
            RpcClientTransport rpcClientTransport = new NettyRpcClient();
            RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
            RpcClientProxy rpcClientProxy = new RpcClientJDKProxy(rpcClientTransport, rpcServiceConfig);
            // RpcClientProxy rpcClientProxy = new RpcClientCglibProxy(rpcClientTransport, rpcServiceConfig);
            HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
            for (int i = 0; i < count; i++) {
                log.info(helloService.hello(new Hello("detailed message" + i, "detailed description" + i)));
                try {
                    // Thread.sleep(6000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            rpcClientTransport.close();
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService executor = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("client-number");
        int sum = 3;
        for (int i = 0; i < sum; i++) {
            executor.execute(new ClientRunnable());
        }
        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);
        log.info("executor closed!");

        // executor = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("client-number");
        // for (int i = 0; i < sum; i++) {
        //     executor.execute(new ClientRunnable());
        // }
        // executor.shutdown();
        //         executor.shutdown();
        // executor.awaitTermination(100, TimeUnit.SECONDS);

    }
}
