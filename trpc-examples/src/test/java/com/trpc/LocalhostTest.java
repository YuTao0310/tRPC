package com.trpc;

import java.net.InetAddress;

import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

public class LocalhostTest {
    
    @Test
    @SneakyThrows
    public void test() {
        String inetAddress = InetAddress.getLocalHost().getHostAddress();
        System.out.println(inetAddress);
    }
}
