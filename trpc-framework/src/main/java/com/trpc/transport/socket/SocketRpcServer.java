package com.trpc.transport.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import com.trpc.config.CustomShutdownHook;
import com.trpc.config.RpcServiceConfig;
import com.trpc.enums.ServiceProviderEnum;
import com.trpc.extension.ExtensionLoader;
import com.trpc.provider.ServiceProvider;
import com.trpc.transport.RpcServerTransport;
import com.trpc.utils.threadpool.ThreadPoolFactoryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketRpcServer implements RpcServerTransport{

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;

    private String host;
    private int port;


    // public static final String DEFAULT_HOST = InetAddress.getLocalHost().getHostAddress()

    public SocketRpcServer() {
        threadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getExtension(ServiceProviderEnum.ZK.getName());;
    }

    @Override
    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
        this.host = rpcServiceConfig.getHost();
        this.port = rpcServiceConfig.getPort();
    }

    @Override
    public void start() {
        CustomShutdownHook.clearAll(host, port);
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

