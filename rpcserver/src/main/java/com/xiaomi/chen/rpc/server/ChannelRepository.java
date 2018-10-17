package com.xiaomi.chen.rpc.server;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/10
 * @description
 */
public class ChannelRepository {

    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();


    public ChannelRepository put(String key, Channel channel){
        channelMap.put(key, channel);
        return this;
    }

    public Channel get(String key){
        return channelMap.get(key);
    }

    public void remove(String key){
        this.channelMap.remove(key);
    }

    public int size(){
        return channelMap.size();
    }
}
