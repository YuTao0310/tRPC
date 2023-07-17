package com.trpc.client;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.trpc.annotation.RpcScan;

@SuppressWarnings("resource")
@RpcScan(basePackage = {"com.trpc"})
public class SpringNettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringNettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}
