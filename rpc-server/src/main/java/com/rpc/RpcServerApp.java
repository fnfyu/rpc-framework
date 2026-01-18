package com.rpc;

import com.rpc.server.NettyServerTransport;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcServerApp implements CommandLineRunner {
    private final NettyServerTransport transport;

    // Spring 会自动把配置好的 serverTransport 注入进来
    public RpcServerApp(NettyServerTransport transport) {
        this.transport = transport;
    }

    public static void main(String[] args) {
        SpringApplication.run(RpcServerApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 当 Spring 把所有 Bean（包括你的服务）都注册好后，启动 Netty
        transport.start();
    }
}
