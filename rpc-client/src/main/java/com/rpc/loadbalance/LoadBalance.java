package com.rpc.loadbalance;

import java.util.List;

public interface LoadBalance {
    String selectServiceAddress(List<String> serviceAddressList);
}
