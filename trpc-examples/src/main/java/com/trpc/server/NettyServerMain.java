package com.trpc.server;

import java.util.concurrent.ExecutorService;

import com.trpc.config.RpcServiceConfig;
import com.trpc.hello.HelloService;
import com.trpc.server.serviceImpl.HelloServiceImpl;
import com.trpc.transport.RpcServerTransport;
import com.trpc.transport.netty.server.NettyRpcServer;
import com.trpc.transport.socket.SocketRpcServer;
import com.trpc.utils.threadpool.ThreadPoolFactoryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerMain {

    private static class ServerRunnable implements Runnable {

        private final RpcServiceConfig rpcServiceConfig;

        public ServerRunnable(RpcServiceConfig rpcServiceConfig) {
            this.rpcServiceConfig  = rpcServiceConfig;
        }

        @Override
        public void run() {
            log.info("host is " + rpcServiceConfig.getHost() + " port is " + rpcServiceConfig.getPort());
            RpcServerTransport rpcServerTransport = new NettyRpcServer();
            rpcServerTransport.registerService(rpcServiceConfig);  
            rpcServerTransport.start();
        }
    }
    public static void main(String[] args) {
        ExecutorService executors = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("server-number");
        int start = 9990, count = 1;
        HelloService helloService = new HelloServiceImpl();
        for (int i = 0; i < count; i++) {
            RpcServiceConfig rpcServiceConfig = new RpcServiceConfig();
            rpcServiceConfig.setHost("127.0.0.1");
            rpcServiceConfig.setService(helloService);
            rpcServiceConfig.setPort(start + i);
            // 每一个server线程对应一个新的RpcServiceConfig实例，不要采用只是每次setPort方式来修改端口
            executors.execute(new ServerRunnable(rpcServiceConfig));
            
        }
    }
}

