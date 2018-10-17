package com.xiaomi.chen.rpc.client.old.netty;

import com.xiaomi.chen.rpc.client.old.DefaultFuture;
import com.xiaomi.chen.rpc.common.domain.RpcRequest;
import com.xiaomi.chen.rpc.common.domain.RpcResponse;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/9
 * @description
 */
@Slf4j
public class ClientHandler extends ChannelDuplexHandler{

    private final Map<String, DefaultFuture> futureMap = new ConcurrentHashMap<>();


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        if(msg instanceof RpcRequest){
            RpcRequest request = (RpcRequest) msg;
            futureMap.putIfAbsent(request.getRequestId(), new DefaultFuture());
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof RpcResponse){
            RpcResponse response = (RpcResponse) msg;
            DefaultFuture future = futureMap.get(response.getRequestId());
            if(future != null){
                future.setResponse(response);
            }
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ClientHandler exception:",cause);
        super.exceptionCaught(ctx, cause);
    }


    public RpcResponse getRpcResponse(String requestId){

        try {
            DefaultFuture defaultFuture = futureMap.get(requestId);
            return defaultFuture.getResponse(1000);
        }finally {
            futureMap.remove(requestId);
        }
    }
}
