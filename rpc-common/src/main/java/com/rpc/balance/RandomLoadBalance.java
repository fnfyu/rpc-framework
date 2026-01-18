package com.rpc.balance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceAddressList) {
        Random r = new Random();
        return serviceAddressList.get(r.nextInt(serviceAddressList.size()));
    }
}
