package com.xiaomi.chen.rpc;

import com.xiaomi.chen.rpc.registry.NewServiceDiscovery;
import com.xiaomi.chen.rpc.registry.NewServiceRegister;
import com.xiaomi.chen.rpc.registry.ServiceDiscovery;
import com.xiaomi.chen.rpc.registry.ServiceRegister;
import com.xiaomi.chen.rpc.registry.base.Port;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/9/28
 * @description
 */
public class TestMain {

    public static void main(String[] args) {

        Port port = new Port();
        port.setInterfaceName("com.xiaomi.chen.tt");
        port.setIp("10.235.34.1344");
        port.setPort(8080);

        String registerAddress = "localhost:2181";

//        ServiceRegister registry = new ServiceRegister("localhost:2181");
//
//        registry.register(port);
//
//        ServiceDiscovery discovery = new ServiceDiscovery("localhost:2181");
//
//        System.out.println(discovery.discover(port.getInterfaceName()));
//
//        System.out.println("hah");

        NewServiceRegister register = new NewServiceRegister(registerAddress);

        register.register(port);

        NewServiceDiscovery discovery = new NewServiceDiscovery(registerAddress);
        System.out.println(discovery.discover(port.getInterfaceName()));


    }
}
