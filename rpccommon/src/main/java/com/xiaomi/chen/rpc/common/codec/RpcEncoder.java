package com.xiaomi.chen.rpc.common.codec;

import com.xiaomi.chen.rpc.common.util.JsonSerializer;
import com.xiaomi.chen.rpc.common.util.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/9
 * @description
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> clz;
    private Serialization serialization;


    public RpcEncoder(Class<?> clz) {
        this.clz = clz;
        this.serialization = new JsonSerializer();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(clz != null){
            byte[] data= serialization.serialize(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}
