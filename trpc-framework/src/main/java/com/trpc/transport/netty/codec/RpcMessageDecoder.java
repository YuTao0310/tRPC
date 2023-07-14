package com.trpc.transport.netty.codec;

import java.util.List;

import com.trpc.dto.RpcRequest;
import com.trpc.dto.RpcResponse;
import com.trpc.serialize.Serializer;
import com.trpc.serialize.hessian.HessianSerializer;
import com.trpc.utils.singleton.SingletonFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class RpcMessageDecoder extends ByteToMessageDecoder {

    @Override
     protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        Serializer serializer = SingletonFactory.getInstance(HessianSerializer.class);
        byte dataType = in.readByte();
        byte[] bs = new byte[in.readInt()];
        in.readBytes(bs);
        if (dataType == 0) {
            out.add(serializer.deserialize(bs, RpcRequest.class));
        } else {
            out.add(serializer.deserialize(bs, RpcResponse.class));
        }
     }
    
}
