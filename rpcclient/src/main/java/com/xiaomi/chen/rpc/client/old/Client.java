package com.xiaomi.chen.rpc.client.old;

import com.xiaomi.chen.rpc.common.domain.RpcRequest;
import com.xiaomi.chen.rpc.common.domain.RpcResponse;

import java.io.Closeable;
import java.net.InetSocketAddress;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/9
 * @description
 */
public interface Client extends Closeable{

    RpcResponse send(RpcRequest request);

    void connect(InetSocketAddress socketAddress);

    InetSocketAddress getSocketAddress();
}
