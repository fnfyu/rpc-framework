package com.rpc.spring;

import com.rpc.annotation.RpcService;
import com.rpc.registry.ServiceRegistry;
import com.rpc.server.NettyServerTransport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class RpcServerProvider implements BeanPostProcessor {
    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private NettyServerTransport transport;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 1. 检查这个类上是否有 @RpcService 注解
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            // 2. 拿到它实现的接口名称（比如 HelloService）
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            if (interfaces.length == 0) {
                throw new RuntimeException("服务类必须至少实现一个接口!");
            }
            String interfaceName = interfaces[0].getName();
            // 3. 注册到我们 NettyServer 的本地 Map 里
            transport.registerService(interfaceName, bean);

            // 4. 注册到 Zookeeper 远程中心
            serviceRegistry.register(interfaceName,new InetSocketAddress("127.0.0.1",transport.getPort()));
            System.out.println(">>> [Spring自动注册] 服务: " + interfaceName + " 已就绪");
        }
        return bean;
    }
}
