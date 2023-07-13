package com.trpc.dto;

import java.io.Serializable;

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

public class RpcRequest implements Serializable{
    private static final long serialVersionUID = 1234567890L;
    /**
     * request unique Id
     */
    private String requestId;
    /**
     * remote interface name
     */
    private String interfaceName;
    /**
     * remote method name
     */
    private String methodName;
    /**
     * parameter values
     */
    private Object[] parameters;
    /**
     * parameter types
     */
    private Class<?> [] paramTypes;
    /**
     * version of related implementations in server
     */
    private String version;
    /**
     * different implementations in server
     * when the interface has multiple implementation classes, distinguish by group
     */
    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }    
}
