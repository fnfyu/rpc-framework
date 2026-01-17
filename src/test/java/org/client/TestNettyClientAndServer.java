package org.client;

import org.junit.Test;
import org.registry.LocalServiceRegistry;
import org.registry.ServiceRegistry;

import java.net.InetSocketAddress;

public class TestNettyClientAndServer {
    @Test
    public void testNettyClientAndServer() throws InterruptedException {
        // 1. 同一个中介对象
        ServiceRegistry registry = new LocalServiceRegistry();

        // 2. 启动服务端并在同一线程（或新线程）登记
        new Thread(() -> {
            // 启动 Server 的逻辑...
            registry.register("org.service.HelloService", new InetSocketAddress("127.0.0.1", 12345));
        }).start();

        // 3. 稍等 1 秒确保登记成功
        Thread.sleep(1000);

        // 4. 客户端查找
        InetSocketAddress addr = registry.lookupService("org.service.HelloService");
        System.out.println("查到了吗？" + addr);
    }
}
