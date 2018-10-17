package com.xiaomi.chen.rpc.server.netty;

import com.xiaomi.chen.rpc.common.domain.RpcRequest;
import com.xiaomi.chen.rpc.common.domain.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/10
 * @description
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final Map<String, Object> handlerMap;


    public ServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object handler = handler(request);
            response.setResult(handler);
            log.debug("---------channel read response:{}",response);
        } catch (Throwable throwable) {
            response.setThrowable(throwable);
            throwable.printStackTrace();
        }
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    private Object handler(RpcRequest request) throws InvocationTargetException {
        Object serviceBean = handlerMap.get(request.getClassName());
        if(serviceBean == null){
            return null;
        }

        Class<?> serviceClz = serviceBean.getClass();
        FastClass fastClass = FastClass.create(serviceClz);
        FastMethod fastMethod = fastClass.getMethod(request.getMethodName(), request.getParameterTypes());
        Object ret = fastMethod.invoke(serviceBean, request.getParameters());
        log.debug("service handler return:{}",ret);
        return ret;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server caught exception", cause);
        if(ctx.channel().isActive()){
            ctx.close();
        }
    }
}
