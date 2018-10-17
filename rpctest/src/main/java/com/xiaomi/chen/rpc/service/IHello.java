package com.xiaomi.chen.rpc.service;

import com.xiaomi.chen.rpc.common.annotation.RpcInterface;

/**
 * Created by Wei Chen on 15:52 2018/10/10.
 */
@RpcInterface
public interface IHello {
    String sayHi(String msg, int code);

}
