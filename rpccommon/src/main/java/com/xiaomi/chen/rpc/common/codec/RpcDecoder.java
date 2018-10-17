package com.xiaomi.chen.rpc.common.codec;

import com.xiaomi.chen.rpc.common.util.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/9
 * @description
 */
public class RpcDecoder extends ByteToMessageDecoder{

    private Class<?> clz;

    private Serialization serialization;

    public RpcDecoder(Class<?> clz, Serialization serialization) {
        this.clz = clz;
        this.serialization = serialization;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < 4){
            return;
        }

        byteBuf.markReaderIndex();

        int dataLength = byteBuf.readInt();
        if(byteBuf.readableBytes() < dataLength){
            byteBuf.resetReaderIndex();
            return;
        }

        byte[]data = new byte[dataLength];
        byteBuf.readBytes(data);

        Object obj = serialization.deserialize(data, clz);

        list.add(obj);
    }
}
