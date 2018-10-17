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
public class RpcRequest implements Serializable{

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}
