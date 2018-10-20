package com.xiaomi.chen.rpc.server.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaomi.chen.rpc.common.domain.RpcRequest;
import com.xiaomi.chen.rpc.common.domain.RpcResponse;
import com.xiaomi.chen.rpc.common.util.JSONUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
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

            Object ll = null;
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

        Class<?>[] types = request.getParameterTypes();
        Object[] params = request.getParameters();
        Object[] pas = null;
        if(params != null && params.length >0){
            pas = revertParams(types, params);
            System.out.println("-----"+JSONUtil.toJSONString(pas));

        }

        Object ret = fastMethod.invoke(serviceBean, pas);

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


    // 在这里转一下请求参数
    public Object[] revertParams(Class<?>[] parameterTypes, Object[] parameters){

        Object[] ret = new Object[parameterTypes.length];

        for(int i=0; i<parameters.length;i++){
            if(parameters[i] instanceof LinkedHashMap){
                String str = JSONUtil.toJSONString(parameters[i]);
                ret[i] = JSONUtil.parseObject(str, parameterTypes[i]);
            }else{
                ret[i] = parameters[i];
            }

        }
        return ret;

    }

}
