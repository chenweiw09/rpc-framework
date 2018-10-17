package com.xiaomi.chen.rpc.registry.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/16
 * @description
 */
@Data
public class Port {

    private String interfaceName;

    private String ip;

    private int port;

}
