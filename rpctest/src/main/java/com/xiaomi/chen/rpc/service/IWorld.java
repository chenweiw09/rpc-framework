package com.xiaomi.chen.rpc.service;

import com.xiaomi.chen.rpc.common.annotation.RpcInterface;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/18
 * @description
 */
@RpcInterface
public interface IWorld {

    String say(String s, int g);

    void hsay(String s);

    void dd();

    int todo(Person person);
}
