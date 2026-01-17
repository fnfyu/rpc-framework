package org.registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry {
    //服务端调用，注册服务
    void register(String serviceName, InetSocketAddress inetSocketAddress);

    //客户端调用，查询服务
    InetSocketAddress lookupService(String serviceName);
}
