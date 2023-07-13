package com.trpc.transport.netty;

import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import com.trpc.dto.RpcRequest;
import com.trpc.dto.RpcResponse;
import com.trpc.register.ServiceDiscovery;
import com.trpc.register.zk.ZkServiceDiscoveryImpl;
import com.trpc.transport.RpcClientTransport;
import com.trpc.utils.singleton.SingletonFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClient implements RpcClientTransport {

    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyRpcClient() {
        serviceDiscovery = SingletonFactory.getInstance(ZkServiceDiscoveryImpl.class);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new ObjectEncoder());
                        p.addLast(new ObjectDecoder(Class::forName));             
                        p.addLast(new NettyRpcClientHandler());
                    }
                });
    }

    @Override
    @SneakyThrows
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        Channel channel = completableFuture.get();

        // ChannelFuture f = bootstrap.connect(inetSocketAddress).addListener(future -> {
        //     if (future.isSuccess()) {
        //         log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
        //     } else {
        //         log.info("The client has connected [{}] unsuccessful!", inetSocketAddress.toString());
        //     }
        // }).sync();
        // Channel channel = f.channel();
        channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("client send message: [{}]", rpcRequest);
            } else {
                future.channel().close();
                resultFuture.completeExceptionally(future.cause());
                log.error("Send failed:", future.cause());
            }
        });
        return resultFuture;
    }

    
}
