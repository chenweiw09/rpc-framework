package com.xiaomi.chen.rpc.client.old;

import com.xiaomi.chen.rpc.common.domain.RpcResponse;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/9
 * @description
 */
public class DefaultFuture {

    private RpcResponse response;

    private volatile boolean isSuccessed = false;

    private final Object object = new Object();

    public RpcResponse getResponse(int timeout) {

        synchronized (object){
            try {
                object.wait(timeout);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        return response;
    }


    public void setResponse(RpcResponse response){
        if(isSuccessed){
            return;
        }

        synchronized (object){
            this.response = response;
            this.isSuccessed = true;
            object.notifyAll();
        }
    }
}
