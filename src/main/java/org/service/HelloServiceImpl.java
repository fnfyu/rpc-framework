package org.service;

public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello " + name + "这是来自服务器的问候";
    }
}
