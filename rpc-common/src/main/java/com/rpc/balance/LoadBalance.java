package com.rpc.balance;

import java.util.List;

public interface LoadBalance {
    String selectServiceAddress(List<String> serviceAddressList);
}
