package com.trpc.transport.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import com.trpc.config.RpcServiceConfig;
import com.trpc.provider.ServiceProvider;
import com.trpc.provider.impl.SimpleServiceProviderImpl;
import com.trpc.transport.RpcServerTransport;
import com.trpc.utils.singleton.SingletonFactory;
import com.trpc.utils.threadpool.ThreadPoolFactoryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketRpcServer implements RpcServerTransport{

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;
    public static final int PORT = 9998;


    public SocketRpcServer() {
        threadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(SimpleServiceProviderImpl.class);
    }

    @Override
    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    @Override
    public void start() {
        try (ServerSocket server = new ServerSocket()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, PORT));
            Socket socket;
            while ((socket = server.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                threadPool.execute(new SocketRpcServerHandler(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("occur IOException:", e);
        }
    }

}

