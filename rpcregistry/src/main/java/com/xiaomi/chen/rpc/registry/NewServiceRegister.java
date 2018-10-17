package com.xiaomi.chen.rpc.registry;

import com.xiaomi.chen.rpc.common.constants.Constants;
import com.xiaomi.chen.rpc.common.util.JSONUtil;
import com.xiaomi.chen.rpc.registry.base.Port;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/17
 * @description
 */
@Slf4j
public class NewServiceRegister extends CuratorFactory {

    public NewServiceRegister(String registryAddress) {
        super(registryAddress);
    }

    public void register(Port port){
        if(port !=null){
            log.debug("register port:{}",port);
            try {
                createPersistentNode(getConnection(),  Constants.ZK_REGISTRY_PATH,"root");
                createEphemeralNode(getConnection(), getNodePath(port), JSONUtil.toJSONString(port));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
