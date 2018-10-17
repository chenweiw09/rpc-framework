package com.xiaomi.chen.rpc.common.util;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/9
 * @description
 */
public interface Serialization {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Class<T> clazz);

}
