package com.trpc.transport.netty.codec;

import com.trpc.compress.Compress;
import com.trpc.compress.gzip.GzipCompress;
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
        Compress compress = SingletonFactory.getInstance(GzipCompress.class);
        if (object instanceof RpcRequest) {
            out.writeByte(0);
        } else {
            out.writeByte(1);
        }
        byte[] bs = serializer.serialize(object);
        bs = compress.compress(bs);
        out.writeInt(bs.length);
        out.writeBytes(bs);
    }
}
