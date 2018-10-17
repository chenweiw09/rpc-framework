package com.xiaomi.chen.rpc.common.annotation;

import java.lang.annotation.*;

/**
 * Created by Wei Chen on 11:10 2018/10/10.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RpcInterface {

//    Class<?> value();
    String name()default "";

}
