package com.rpc.registry;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalServiceRegistry implements ServiceRegistry {
    private static final Map<String,InetSocketAddress> serviceMap = new ConcurrentHashMap<>();

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        serviceMap.put(serviceName, inetSocketAddress);
        System.out.println(">>> [中介] 服务已登记:"+serviceName+" 地址:"+inetSocketAddress);
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
            return serviceMap.get(serviceName);
    }
}
