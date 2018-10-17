package com.xiaomi.chen.rpc.common.constants;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/9
 * @description
 */
public class RpcException extends RuntimeException {

    public RpcException(String originalMessage) {
        super(originalMessage);
    }
}
