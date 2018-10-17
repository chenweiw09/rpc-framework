package com.xiaomi.chen.rpc.client.old.proxy;

import com.xiaomi.chen.rpc.client.old.Transporters;
import com.xiaomi.chen.rpc.common.domain.RpcRequest;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/10
 * @description
 */
public class ProxyFactory {

    private Transporters transporters;

    public ProxyFactory(Transporters transporters) {
        this.transporters = transporters;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass){

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, (proxy, method, args) -> {
            RpcRequest request = new RpcRequest();

            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            request.setRequestId(UUID.randomUUID().toString());
            return this.transporters.send(request).getResult();
        });
    }



}
