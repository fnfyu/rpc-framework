package com.rpc.service.impl;

import com.rpc.annotation.RpcService;
import com.rpc.service.HelloService;

@RpcService
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {

        try { Thread.sleep(10000); } catch (InterruptedException e) {}
        return "Spring环境下，你好 " + name;
    }
}
