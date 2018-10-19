package com.xiaomi.chen.rpc.service;

import com.xiaomi.chen.rpc.common.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/18
 * @description
 */

@Slf4j
@RpcService(IWorld.class)
public class IWorldzImpl implements IWorld {

    @Override
    public String say(String s, int g) {
        log.info("---say");
        return "------------"+s+g;
    }

    @Override
    public void hsay(String s) {
        log.info("---hsay"+s);

    }

    @Override
    public void dd() {
        log.info("--dd");
    }

    @Override
    public int todo(Person person) {
        log.info("---------todo parma:{}",person);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
