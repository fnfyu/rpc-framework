package org.client;

import org.registry.LocalServiceRegistry;
import org.registry.ZkServiceRegistry;
import org.service.HelloService;

public class NettyClient {
    public static void main(String[] args) {
        RpcClientProxy proxy=new RpcClientProxy(new ZkServiceRegistry());

        HelloService helloService=proxy.getProxy(HelloService.class);
        String result=helloService.hello("老人机");
        System.out.println("最终结果:"+result);
//         result=helloService.hello("打火机");
//        System.out.println("最终结果:"+result);
//         result=helloService.hello("edg");
//        System.out.println("最终结果:"+result);
//        result=helloService.hello("bb鸡");
//        System.out.println("最终结果:"+result);

        //proxy.shutdown();
    }
}
