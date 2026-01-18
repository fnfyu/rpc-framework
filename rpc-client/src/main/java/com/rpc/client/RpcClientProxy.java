package com.rpc.client;

import com.rpc.dto.RpcRequest;
import com.rpc.registry.ServiceRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RpcClientProxy implements InvocationHandler {
    private ServiceRegistry registry;
    private NettyClientTransport transport=new NettyClientTransport();

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
                method.getParameterTypes(),
                false
        );



        int retryTimes = 3;
        Exception lastException = null;
        for (int i = 0; i < retryTimes; i++) {
            CompletableFuture<Object> future = new CompletableFuture<>();
            UnprocessedRequests.put(requestId, future);
            // 2. 启动 Netty 客户端发包
            InetSocketAddress inetSocketAddress=registry.lookupService(serviceName);
            if (inetSocketAddress==null){
                throw new RuntimeException("错误：中介那没查到这个服务的地址！");
            }

            transport.send(rpcRequest,inetSocketAddress.getHostName(),inetSocketAddress.getPort());

            try {
                return future.get(5, TimeUnit.SECONDS);

            } catch (TimeoutException e) {
                lastException = e;
                System.out.println(">>> [重试] 第 " + (i + 1) + " 次调用失败，原因: " + lastException.getMessage());
                // 失败了要及时把没用的存根清理掉，防止内存泄漏
                if(i<retryTimes-1){
                    Thread.sleep(200);
                }
            }catch (Exception e) {
                e.printStackTrace();
            };

        }

        UnprocessedRequests.remove(requestId);
        throw new RuntimeException("服务调用最终失败，重试次数已达上限", lastException);
    }

}
