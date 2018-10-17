package com.xiaomi.chen.rpc.common.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/16
 * @description
 */
public class JSONUtil {
    private static ObjectMapper mapper = null;

    static {
        mapper = new ObjectMapper();
        mapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
    }

    public static String toJSONString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("toJSONString|object to json string exception:"+ e);
        }

    }


    public static <T> T parseObject(String str, Class<T> clazz) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        try {
            return mapper.readValue(str, clazz);
        } catch (IOException e) {
            throw new RuntimeException("parseObject|to object exception:"+ e);
        }

    }
}
