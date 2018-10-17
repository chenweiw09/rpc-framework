package com.xiaomi.chen.rpc.server.netty;

import com.xiaomi.chen.rpc.common.codec.RpcDecoder;
import com.xiaomi.chen.rpc.common.codec.RpcEncoder;
import com.xiaomi.chen.rpc.common.domain.RpcRequest;
import com.xiaomi.chen.rpc.common.domain.RpcResponse;
import com.xiaomi.chen.rpc.common.util.JsonSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/10
 * @description
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {


    private ServerHandler serverHandler;

    public ServerInitializer(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,0,4));
        pipeline.addLast(new RpcEncoder(RpcResponse.class,new JsonSerializer()));
        pipeline.addLast(new RpcDecoder(RpcRequest.class,new JsonSerializer()));
        pipeline.addLast(serverHandler);
    }
}
