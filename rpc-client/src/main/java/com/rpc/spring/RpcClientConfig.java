package com.rpc.spring;

import com.rpc.client.RpcClientProxy;
import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.ZkServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.rpc")
public class RpcClientConfig {
    @Bean
    public ServiceRegistry serviceRegistry(){
        return new ZkServiceRegistry();
    }

    @Bean
    public RpcClientProxy rpcClientProxy(ServiceRegistry serviceRegistry){
        return new RpcClientProxy(serviceRegistry);
    }
}
