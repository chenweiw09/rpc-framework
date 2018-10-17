package com.xiaomi.chen.rpc.client.old.proxy;

import com.xiaomi.chen.rpc.client.old.Transporters;
import com.xiaomi.chen.rpc.common.domain.RpcRequest;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.util.UUID;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/16
 * @description
 */
public class CglibProxy {

    private Transporters transporters;

    public CglibProxy(Transporters transporters) {
        this.transporters = transporters;
    }

    public <T> T create(Class<T> interfaceClass){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> {
            RpcRequest request = new RpcRequest();

            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(objects);
            request.setRequestId(UUID.randomUUID().toString());
            return transporters.send(request).getResult();
        });



        return (T) enhancer.create();
    }
}
