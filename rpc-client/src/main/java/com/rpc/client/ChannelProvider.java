package com.rpc.client;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelProvider {
    private static Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

    public static Channel get(String host, int port) {
        String key = host + ":" + port;
        if(channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if(channel!=null && channel.isActive()) {
                return channel;
            }
            channels.remove(key);
        }
        return null;
    }

    public static void set(String host, int port, Channel channel) {
        channels.put(host + ":" + port, channel);
    }
}
