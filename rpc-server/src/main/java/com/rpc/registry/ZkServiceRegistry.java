package com.rpc.registry;

import com.rpc.config.RpcConfig;
import jakarta.annotation.PreDestroy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ZkServiceRegistry implements ServiceRegistry {
    private final CuratorFramework curator;
    private static final String ROOT_PATH = "/rpc";

    private static final Set<String>REGISTERED_PATH_SET= ConcurrentHashMap.newKeySet();

    public ZkServiceRegistry() {
        //连接到zk
        this.curator = CuratorFrameworkFactory.builder()
                .connectString(RpcConfig.zkAddress)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        this.curator.start();
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            // 2. 创建路径，比如 /rpc/org.service.HelloService/127.0.0.1:12345
            String path = ROOT_PATH + "/" + serviceName+inetSocketAddress;

            // 要创建“临时节点”（EPHEMERAL）
            // 这样如果服务端崩了，这个地址会自动从 ZK 上消失，不会坑客户端
            if (curator.checkExists().forPath(path)==null)
                curator.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(path);

            System.out.println(">>> [Zookeeper] 服务注册成功: " + path);
            REGISTERED_PATH_SET.add(path);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void clearRegistry(){
        System.out.println(">>> [优雅停机] 开始注销所有已注册的服务...");
        for (String path : REGISTERED_PATH_SET) {
            try {
                curator.delete().forPath(path);
            } catch (Exception e) {
                System.out.println("服务注销失败 "+path);
            }
        }
        curator.close();
    }


    @PreDestroy
    public void destroy() {
        clearRegistry();
    }

}
