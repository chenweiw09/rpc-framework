package com.xiaomi.chen.rpc.common.constants;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/10
 * @description
 */
public class Constants {

    public static final int ZK_SESSION_TIMEOUT = 100000;

    public static final int ZK_CONNECT_TIMEOUT = 100000;

    public static final String ZK_REGISTRY_PATH = "/RPC_REGISTRY";
    public static final String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/node";

    public static final int SERVER_PORT = 8080;
}
