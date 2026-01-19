package com.rpc.loadbalance;

import java.util.List;

public class RoundRobinLoadBalance implements LoadBalance {
    private int idx=0;
    @Override
    public synchronized String selectServiceAddress(List<String> serviceAddressList) {
        if(idx>=serviceAddressList.size()){
            idx=0;
        }
        return serviceAddressList.get(idx++);
    }
}
