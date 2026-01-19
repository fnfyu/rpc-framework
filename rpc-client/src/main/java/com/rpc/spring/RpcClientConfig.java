package com.rpc.spring;

import com.rpc.client.RpcClientProxy;
import com.rpc.discovery.ServiceDiscovery;
import com.rpc.discovery.ZKServiceDiscovery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.rpc")
public class RpcClientConfig {
    @Bean
    public ServiceDiscovery serviceRegistry(){
        return new ZKServiceDiscovery();
    }

    @Bean
    public RpcClientProxy rpcClientProxy(ServiceDiscovery serviceRegistry){
        return new RpcClientProxy(serviceRegistry);
    }
}
