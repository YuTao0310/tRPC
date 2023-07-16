package com.trpc.transport.netty.codec;

import java.util.Arrays;
import java.util.List;

import com.trpc.compress.Compress;
import com.trpc.constants.RpcConstants;
import com.trpc.dto.RpcMessage;
import com.trpc.dto.RpcRequest;
import com.trpc.dto.RpcResponse;
import com.trpc.enums.CompressTypeEnum;
import com.trpc.enums.SerializationTypeEnum;
import com.trpc.serialize.Serializer;
import com.trpc.utils.singleton.SingletonFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * custom protocol decoder
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 * 
 */

@Slf4j
public class RpcMessageDecoder extends ByteToMessageDecoder {

    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in , List<Object> out) throws Exception {
        decodeFrame(in, out);
    }


    private void decodeFrame(ByteBuf in, List<Object> out) {
        // note: must read ByteBuf in order
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();
        // build RpcMessage object
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            out.add(rpcMessage);
            return ;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            out.add(rpcMessage);
            return ;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            try {
                byte[] bs = new byte[bodyLength];
                in.readBytes(bs);
                // decompress the bytes
                String compressName = CompressTypeEnum.getName(compressType);
                Compress compress = (Compress)SingletonFactory.getInstance(Class.forName(compressName));
                bs = compress.decompress(bs);
                // deserialize the object
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("codec name: [{}] ", codecName);
                Serializer serializer = (Serializer)SingletonFactory.getInstance(Class.forName(codecName));
                if (messageType == RpcConstants.REQUEST_TYPE) {
                    RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
                    rpcMessage.setData(tmpValue);
                } else {
                    RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                    rpcMessage.setData(tmpValue);
                }
                out.add(rpcMessage);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkVersion(ByteBuf in) {
        // read the version and compare
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // read the first 4 bit, which is the magic number, and compare
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }
}
