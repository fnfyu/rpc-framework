package com.rpc.service;

import com.rpc.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @RpcReference
    private HelloService helloService;

    public void test() throws InterruptedException {
        while(true) {
            String res = helloService.hello("老人机");
            System.out.println(">>> 远程调用返回结果: " + res);
            Thread.sleep(200);
        }
    }


}
