package org.client;

import org.service.HelloService;

public class NettyClient {
    public static void main(String[] args) {
        RpcClientProxy proxy=new RpcClientProxy("127.0.0.1", 12345);

        HelloService helloService=proxy.getProxy(HelloService.class);
        String result=helloService.hello("老人机");
        System.out.println("最终结果:"+result);
         result=helloService.hello("打火机");
        System.out.println("最终结果:"+result);
         result=helloService.hello("edg");
        System.out.println("最终结果:"+result);
        result=helloService.hello("bb鸡");
        System.out.println("最终结果:"+result);

        proxy.shutdown();
    }
}
