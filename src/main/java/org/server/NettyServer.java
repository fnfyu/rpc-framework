package org.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.dto.RpcRequest;
import org.dto.RpcResponse;
import org.registry.LocalServiceRegistry;
import org.registry.ServiceRegistry;
import org.registry.ZkServiceRegistry;
import org.serializer.KryoSerializer;
import org.serializer.MyRpcDecoder;
import org.serializer.MyRpcEncoder;
import org.service.HelloService;
import org.service.HelloServiceImpl;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class NettyServer {
    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup();
        ServiceRegistry serviceRegistry=new ZkServiceRegistry();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel ch)  {
                    ch.pipeline().addLast(new MyRpcDecoder(new KryoSerializer(),RpcRequest.class));//对象解码器
                    ch.pipeline().addLast(new MyRpcEncoder(new KryoSerializer()));//对象编码器 回信用
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<RpcRequest>() {
                        private static final Map<String,Object> serviceMap=new HashMap<>();
                        static {
                            serviceMap.put("org.service.HelloService",new HelloServiceImpl());
                        }
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
                            System.out.println("[服务器收到远程调用请求]:");
                            System.out.println("接口:"+rpcRequest.getInterfaceName());
                            System.out.println("方法:"+rpcRequest.getMethodName());

                            Object service = serviceMap.get(rpcRequest.getInterfaceName());

                            Method method=service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());

                            Object result=method.invoke(service,rpcRequest.getParams());

                            RpcResponse rpcResponse=new RpcResponse(
                                    rpcRequest.getRequestId(),
                                    result
                            );
                            channelHandlerContext.channel().writeAndFlush(rpcResponse).sync();
                            System.out.println(">>方法调用已完成,结果已回传"+result);

                        }
                    });
                }
            });
            serviceRegistry.register(HelloService.class.getName(),new InetSocketAddress("127.0.0.1",12345));
            System.out.println(">>>RPC服务端已启动，监听端口:12345...");
            ChannelFuture future= b.bind(12345).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }

    }
}
