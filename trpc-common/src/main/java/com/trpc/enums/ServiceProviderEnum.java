package com.trpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServiceProviderEnum {
    
    ZK("zk");
    private String name;
}
