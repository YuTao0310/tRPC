package com.trpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LoadBalanceEnum {

    LOADBALANCE("loadBalance");

    private final String name;
}
