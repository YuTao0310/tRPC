package com.trpc.transport.netty.codec;

import com.trpc.dto.RpcRequest;
import com.trpc.serialize.Serializer;
import com.trpc.serialize.hessian.HessianSerializer;
import com.trpc.utils.singleton.SingletonFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcMessageEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object object, ByteBuf out) {
        Serializer serializer = SingletonFactory.getInstance(HessianSerializer.class);
        if (object instanceof RpcRequest) {
            out.writeByte(0);
        } else {
            out.writeByte(1);
        }
        byte[] bs = serializer.serialize(object);
        out.writeInt(bs.length);
        out.writeBytes(bs);
    }
}
