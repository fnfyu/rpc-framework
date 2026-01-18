package com.rpc.spring;

import com.rpc.annotation.RpcReference;
import com.rpc.client.RpcClientProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class RpcClientProcessor implements BeanPostProcessor {
    @Autowired
    private RpcClientProxy rpcClientProxy;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 1. 拿到这个类的所有成员变量
        Field[] fields =bean.getClass().getDeclaredFields();
        for(Field f: fields){
            // 2. 检查变量上是否有 @RpcReference 注解
            if(f.isAnnotationPresent(RpcReference.class)){
                // 3. 利用动态代理，造一个代理对象
                Object proxy=rpcClientProxy.getProxy(f.getType());//getType是get声明的类型

                // 4. 把代理对象“强行”塞给这个字段
                f.setAccessible(true);
                try {
                    f.set(bean,proxy);
                    System.out.println(">>> [Spring自动注入] 字段: " + f.getName() + " 已注入 RPC 代理");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
