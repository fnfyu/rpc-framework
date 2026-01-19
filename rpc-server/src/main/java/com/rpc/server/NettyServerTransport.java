package com.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import com.rpc.dto.RpcRequest;
import com.rpc.dto.RpcResponse;
import com.rpc.serializer.MyRpcDecoder;
import com.rpc.serializer.MyRpcEncoder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NettyServerTransport {
    private static final EventLoopGroup boss = new NioEventLoopGroup(1);
    private static final EventLoopGroup work = new NioEventLoopGroup();
    private static final Map<String,Object> serviceMap=new HashMap<>();
    @Getter
    private int port;


    public NettyServerTransport(int port) {
        this.port = port;
    }

    public void registerService(String serviceName,Object serviceImpl){
        serviceMap.put(serviceName,serviceImpl);
    }

    public void start() throws InterruptedException {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, work).channel(NioServerSocketChannel.class).
                    childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MyRpcEncoder());
                            socketChannel.pipeline().addLast(new MyRpcDecoder(RpcRequest.class));
                            socketChannel.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS)); // 30秒没收到客户端消息，就触发“读空闲”
                            socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<RpcRequest>() {
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    if (evt instanceof IdleStateEvent) {
                                        IdleState state = ((IdleStateEvent) evt).state();
                                        if(state== IdleState.READER_IDLE){
                                            System.out.println(">>> [心跳监测] 30秒没收到客户端消息了，主动断开连接以节省资源");
                                            ctx.close();
                                        }
                                    }
                                    else {
                                        super.userEventTriggered(ctx, evt);
                                    }
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
                                    if( rpcRequest.isHeartbeat()){
                                        System.out.println(">>> [心跳] 收到客户端心跳，确认存活。");
                                        return;
                                    }

                                    Object service=serviceMap.get(rpcRequest.getInterfaceName());
                                    if (service==null) {
                                        throw new RuntimeException("未找到服务");
                                    }

                                    Method method=service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
                                    Object result=method.invoke(service,rpcRequest.getParams());

                                    RpcResponse rpcResponse=new RpcResponse();
                                    rpcResponse.setResult(result);
                                    rpcResponse.setRequestId(rpcRequest.getRequestId());

                                    channelHandlerContext.channel().writeAndFlush(rpcResponse);
                                    System.out.println(">>> 结果已回传: " + result);

                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    System.err.println(">>> 服务端处理出错: " + cause.getMessage());
                                    ctx.close();
                                }
                            });
                        }
                    });

            ChannelFuture f=b.bind(port).sync();
            System.out.println(">>> RPC服务端已启动，监听端口: " + port);
            f.channel().closeFuture().sync();
        }
        finally {
            stop();
        }
    }

    public void stop(){
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }

}
