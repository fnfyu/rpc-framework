package com.rpc.loadbalance;

import com.rpc.config.RpcConfig;
import com.rpc.extension.ExtensionLoader;

public class LoadBalanceFactory {
    public static LoadBalance getLoadBalance() {
        return ExtensionLoader.getExtension(LoadBalance.class,RpcConfig.loadBalance);
    }
//        String loadBlance= RpcConfig.loadBalance;
//        switch (loadBlance) {
//            case "RoundRobin":
//                return new RoundRobinLoadBalance();
//            case "Random":
//            default:
//                return new RandomLoadBalance();
//        }
//    }
}

