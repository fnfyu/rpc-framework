package org.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.balance.LoadBalance;
import org.balance.RoundRobinLoadBalance;

import javax.management.ServiceNotFoundException;
import java.net.InetSocketAddress;
import java.util.List;

public class ZkServiceRegistry implements ServiceRegistry {
    private final CuratorFramework curator;
    private static final String ROOT_PATH = "/registry";
    private final LoadBalance loadBalance=new RoundRobinLoadBalance();

    public ZkServiceRegistry() {
        //连接到zk
        this.curator = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        this.curator.start();
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            // 2. 创建路径，比如 /netty-rpc/org.service.HelloService/127.0.0.1:12345
            String path = ROOT_PATH + "/" + serviceName+inetSocketAddress;

            // 要创建“临时节点”（EPHEMERAL）
            // 这样如果服务端崩了，这个地址会自动从 ZK 上消失，不会坑客户端
            if (curator.checkExists().forPath(path)==null)
                curator.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(path);

            System.out.println(">>> [Zookeeper] 服务注册成功: " + path);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            // 3. 去 ZK 目录下找这个服务所有的子节点
            List<String> children = curator.getChildren().forPath(ROOT_PATH + "/" + serviceName);
            if (children==null || children.isEmpty())
                throw new ServiceNotFoundException("没有可用的服务节点");
            String addr = loadBalance.selectServiceAddress(children);
            System.out.println(">>> [负载均衡] 选中的服务器地址为: " + addr);
            String[] parts = addr.split(":");
            return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));

        }
        catch (Exception e) {
            throw new RuntimeException(">>> [Zookeeper] 未找到服务: " + serviceName);
        }


    }
}
