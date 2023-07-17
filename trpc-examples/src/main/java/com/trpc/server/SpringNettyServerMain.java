package com.trpc.server;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.trpc.annotation.RpcScan;
import com.trpc.transport.netty.server.NettyRpcServer;

@SuppressWarnings("resource")
@RpcScan(basePackage={"com.trpc"})
public class SpringNettyServerMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringNettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        nettyRpcServer.start();
    }
}
