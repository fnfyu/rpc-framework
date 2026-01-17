package org.client;

import lombok.AllArgsConstructor;
import org.dto.RpcRequest;
import org.registry.ServiceRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RpcClientProxy implements InvocationHandler {
    private ServiceRegistry registry;

    public RpcClientProxy(ServiceRegistry registry) {
        this.registry= registry;
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
        String serviceName = method.getDeclaringClass().getName();
        // 1. 把“谁想调谁”打包成包裹
        RpcRequest rpcRequest = new RpcRequest(
                requestId,
                serviceName,
                method.getName(),
                args,
                method.getParameterTypes()
        );

        CompletableFuture<Object> future = new CompletableFuture<>();
        UnprocessedRequests.put(requestId, future);

        // 2. 启动 Netty 客户端发包
        InetSocketAddress inetSocketAddress=registry.lookupService(serviceName);
        if (inetSocketAddress==null){
            throw new RuntimeException("错误：中介那没查到这个服务的地址！");
        }
        NettyClientTransport transport=new NettyClientTransport(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        transport.send(rpcRequest);

        return future.get();
    }

//    public void shutdown() {
//        try {
//            transport.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
