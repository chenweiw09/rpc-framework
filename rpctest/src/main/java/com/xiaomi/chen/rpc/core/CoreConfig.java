package com.xiaomi.chen.rpc.core;

import com.xiaomi.chen.rpc.client.old.RpcClientConfig;
import com.xiaomi.chen.rpc.client.proxy.RpcProxy;
import com.xiaomi.chen.rpc.registry.ServiceDiscovery;
import com.xiaomi.chen.rpc.server.RpcServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/16
 * @description
 */
@Configuration
public class CoreConfig {

    @Bean
    public RpcClientConfig rpcConfig(){
        return new RpcClientConfig("localhost:2181","com.xiaomi.chen");
    }


    @Bean
    public RpcServerConfig serverConfig(){
        return new RpcServerConfig("localhost:2181");
    }

    @Bean
    ServiceDiscovery serviceDiscovery(){
        return new ServiceDiscovery("localhost:2181");
    }

    @Bean
    RpcProxy rpcProxy(){
        return new RpcProxy(serviceDiscovery());
    }

}
