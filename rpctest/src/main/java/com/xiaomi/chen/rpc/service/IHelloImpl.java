package com.xiaomi.chen.rpc.service;

import com.xiaomi.chen.rpc.common.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/10
 * @description
 */

@Slf4j
@RpcService(IHello.class)
public class IHelloImpl implements IHello {

    @Override
    public String sayHi(String msg, int code) {
        String str = msg+"|-----"+code;
        log.info("sayHi:return "+str);
        return str;
    }
}
