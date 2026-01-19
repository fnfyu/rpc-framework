package com.rpc.discovery;

import com.rpc.loadbalance.LoadBalance;
import com.rpc.loadbalance.LoadBalanceFactory;
import com.rpc.config.RpcConfig;
import jakarta.annotation.PreDestroy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import javax.management.ServiceNotFoundException;
import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceDiscovery implements ServiceDiscovery {
    private final CuratorFramework curator;
    private static final String ROOT_PATH = "/rpc";
    private final LoadBalance loadBalance= LoadBalanceFactory.getLoadBalance();

    public ZKServiceDiscovery() {
        //连接到zk
        this.curator = CuratorFrameworkFactory.builder()
                .connectString(RpcConfig.zkAddress)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        this.curator.start();
    }


    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            // 3. 去 ZK 目录下找这个服务所有的子节点
            List<String> children = curator.getChildren().forPath(ROOT_PATH + "/" + serviceName);

            if (children==null || children.isEmpty())
                System.out.println("[错误:找不到可用的服务节点]");
            String addr = loadBalance.selectServiceAddress(children);
            System.out.println(">>>["+loadBalance.getClass().getName()+"]");
            System.out.println(">>> [负载均衡] 选中的服务器地址为: " + addr);
            String[] parts = addr.split(":");
            return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));

        }
        catch (Exception e) {
            throw new RuntimeException(">>> [Zookeeper] 未找到服务: " + serviceName);
        }


    }

    @PreDestroy
    public void destroy() {
        curator.close();
    }
}
