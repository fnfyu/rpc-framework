package com.rpc.spring;

import com.rpc.config.RpcConfig;
import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.ZkServiceRegistry;
import com.rpc.server.NettyServerTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcServerConfig {
    @Bean
    public ServiceRegistry serviceRegistry(){
        return new ZkServiceRegistry();
    }

    @Bean
    public NettyServerTransport serverTransport(){
        return new NettyServerTransport(RpcConfig.port);
    }

}
