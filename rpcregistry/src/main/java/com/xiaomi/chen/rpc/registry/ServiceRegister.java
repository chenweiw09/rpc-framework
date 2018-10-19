package com.xiaomi.chen.rpc.registry;

import com.xiaomi.chen.rpc.common.constants.Constants;
import com.xiaomi.chen.rpc.common.util.JSONUtil;
import com.xiaomi.chen.rpc.registry.base.NodePort;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/10
 * @description
 */
@Slf4j
public class ServiceRegister extends RpcRegistryFactory {


    public ServiceRegister(String registryAddress) {
        super(registryAddress);
    }

    public void register(String data){
        if(data != null && data.length() >0){
            createRootNode(getConnection(), "root", Constants.ZK_REGISTRY_PATH);
            createEphemeralNode(getConnection(), data, Constants.ZK_DATA_PATH);
        }
    }

    public void register(NodePort port){
        if(port !=null){
            log.debug("register port:{}",port);
            createRootNode(getConnection(), "root", Constants.ZK_REGISTRY_PATH);
            createEphemeralNode(getConnection(), JSONUtil.toJSONString(port), getNodePath(port));
        }
    }

}
