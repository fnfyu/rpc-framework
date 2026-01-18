package com.rpc.server;

import com.rpc.registry.ServiceRegistry;
import com.rpc.registry.ZkServiceRegistry;
import com.rpc.service.HelloService;
import com.rpc.service.impl.HelloServiceImpl;

import java.net.InetSocketAddress;


//public class NettyServer {
//    public static void main(String[] args) throws InterruptedException {
//
//        ServiceRegistry registry = new ZkServiceRegistry();
//        NettyServerTransport server = new NettyServerTransport(12345);
//        server.registerService(HelloService.class.getName(), new HelloServiceImpl());
//        registry.register(HelloService.class.getName(), new InetSocketAddress("127.0.0.1", server.getPort()));
//        server.start();
//
//    }
//}
