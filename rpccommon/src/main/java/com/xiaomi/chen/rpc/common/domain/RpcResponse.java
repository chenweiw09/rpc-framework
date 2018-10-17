package com.xiaomi.chen.rpc.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/9
 * @description
 */
@Data
public class RpcResponse implements Serializable{

    private String requestId;

    private Throwable throwable;

    private Object result;
}
