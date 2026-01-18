package com.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // 作用在类上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
@Component // 关键：让 Spring 扫描到并把它当成一个 Bean
public @interface RpcService {
}
