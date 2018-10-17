package com.xiaomi.chen.rpc.client.old;

import com.xiaomi.chen.rpc.client.old.netty.NettyClient;
import com.xiaomi.chen.rpc.common.constants.RpcException;
import com.xiaomi.chen.rpc.common.domain.RpcRequest;
import com.xiaomi.chen.rpc.common.domain.RpcResponse;
import com.xiaomi.chen.rpc.registry.ServiceDiscovery;
import org.springframework.util.StringUtils;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/10
 * @description
 */
public class Transporters {


    private ServiceDiscovery serviceDiscovery;

    public Transporters(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }


    public RpcResponse send(RpcRequest request){

        if(serviceDiscovery == null){
            throw new RpcException("no serviceDiscovery");
        }

        String serverAddress = serviceDiscovery.discover(request.getClassName());
        if(StringUtils.isEmpty(serverAddress)){
            throw new RpcException("no endpoint found");
        }
        String[] array = serverAddress.split(":");
        NettyClient client = new NettyClient(array[0], Integer.valueOf(array[1]));
        client.connect(client.getSocketAddress());
        return client.send(request);
    }

}
