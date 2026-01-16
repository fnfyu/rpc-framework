package org.client;

import lombok.AllArgsConstructor;
import org.dto.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RpcClientProxy implements InvocationHandler {
    NettyClientTransport transport;

    public  RpcClientProxy(String host, int port) {
        transport = new NettyClientTransport(host, port);
    }

    //它返回一个接口的伪装对象
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> interfaceClass){
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String requestId = UUID.randomUUID().toString();
        // 1. 把“谁想调谁”打包成包裹
        RpcRequest rpcRequest = new RpcRequest(
                requestId,
                method.getDeclaringClass().getName(),
                method.getName(),
                args,
                method.getParameterTypes()
        );

        CompletableFuture<Object> future = new CompletableFuture<>();
        UnprocessedRequests.put(requestId, future);

        // 2. 启动 Netty 客户端发包 (这里我们可以复用之前的 NettyClient 逻辑)

        transport.send(rpcRequest);

        return future.get();
    }

    public void shutdown() {
        try {
            transport.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
