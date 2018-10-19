package com.xiaomi.chen.rpc;

import com.xiaomi.chen.rpc.common.domain.RpcRequest;
import com.xiaomi.chen.rpc.registry.NewServiceRegister;
import com.xiaomi.chen.rpc.registry.ServiceDiscovery;
import com.xiaomi.chen.rpc.registry.base.NodePort;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/9/28
 * @description
 */
public class TestMain {

    public static void main(String[] args) {

//        NodePort port = new NodePort();
//        port.setInterfaceName("com.xiaomi.chen.tt");
//        port.setIp("10.235.34.1344");
//        port.setPort(8080);
//
//        String registerAddress = "localhost:2181";
//
////        ServiceRegister registry = new ServiceRegister("localhost:2181");
////
////        registry.register(port);
////
////        ServiceDiscovery discovery = new ServiceDiscovery("localhost:2181");
////
////        System.out.println(discovery.discover(port.getInterfaceName()));
////
////        System.out.println("hah");
//
//        NewServiceRegister register = new NewServiceRegister(registerAddress);
//
//        register.register(port);
//
////        NewServiceDiscovery discovery = new NewServiceDiscovery(registerAddress);
//        ServiceDiscovery discovery = new ServiceDiscovery("localhost:2181");
//        System.out.println(discovery.discover(port.getInterfaceName()));


        Class clazz = String.class;
        System.out.println(clazz.isMemberClass());

        System.out.println(clazz.isPrimitive());

        System.out.println(clazz.isArray());



    }

    public void vv(Method method, Object[]args){

        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
    }
}
