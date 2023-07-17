package com.trpc.transport;

import com.trpc.dto.RpcRequest;
import com.trpc.extension.SPI;

@SPI
public interface RpcClientTransport {
    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);

    public void close();
}

