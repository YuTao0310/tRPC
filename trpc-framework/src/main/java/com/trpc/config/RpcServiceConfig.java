package com.trpc.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {
    /**
     * service version (in server client)
     */
    @Builder.Default
    private String version = "";
    /**
     * when the interface has multiple implementation classes, distinguish by group (in server client)
     */
    @Builder.Default
    private String group = "";

    /**
     * target service(in server)
     */
    private Object service;

    /**
     * target host (in server)
     */
    @Builder.Default
    private String host = "127.0.0.1";

    /**
     * target port (in server)
     */
    @Builder.Default
    private int port = 9998;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}