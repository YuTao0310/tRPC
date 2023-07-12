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

    public static final int DEFAULT_PORT = 9998;
    public static final String DEFAULT_HOST = "127.0.0.1";

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;

    private int port = DEFAULT_PORT;
    private String host = DEFAULT_HOST;

    // public static final String DEFAULT_HOST = InetAddress.getLocalHost().getHostAddress()

    public SocketRpcServer() {
        threadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(SimpleServiceProviderImpl.class);
    }

    public SocketRpcServer(int port) {
        this();
        this.port = port;
    }

    public SocketRpcServer(String host) {
        this();
        this.host = host;
    }

    public SocketRpcServer(String host, int port) {
        this();
        this.port = port;
        this.host = host;
    }

    @Override
    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    @Override
    public void start() {
        try (ServerSocket server = new ServerSocket()) {
            server.bind(new InetSocketAddress(host, port));
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

