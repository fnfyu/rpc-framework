package com.rpc.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import com.rpc.dto.RpcRequest;
import com.rpc.dto.RpcResponse;
import com.rpc.serializer.KryoSerializer;
import com.rpc.serializer.MyRpcDecoder;
import com.rpc.serializer.MyRpcEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class NettyClientTransport {
    private static final EventLoopGroup group = new NioEventLoopGroup();
    private static final Bootstrap bootstrap = new Bootstrap();

    static {
        bootstrap.group(group).
                channel(NioSocketChannel.class).
                option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000).
                handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline().addLast(new MyRpcEncoder(new KryoSerializer()));
                channel.pipeline().addLast(new MyRpcDecoder(RpcResponse.class));
                channel.pipeline().addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
                channel.pipeline().addLast(new SimpleChannelInboundHandler<RpcResponse>() {
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        if (evt instanceof IdleStateEvent) {
                            IdleState state = ((IdleStateEvent) evt).state();
                            if (state == IdleState.WRITER_IDLE) {
                                System.out.println(">>> [心跳] 5秒没动静了，给服务器发个心跳包...");
                                RpcRequest heartbeat = RpcRequest.builder().heartbeat(true).build();
                                channel.writeAndFlush(heartbeat).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                            }
                            else {
                                super.userEventTriggered(ctx, evt);
                            }
                        }
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
                        //System.out.println("[客户端收到执行结果]"+rpcResponse);
                        UnprocessedRequests.complete(rpcResponse.getRequestId(), rpcResponse.getResult());
                    }
                });
            }
        });

    }


    public void send(RpcRequest rpcRequest,String host, int port) {
        Channel channel;
        try {
            if (ChannelProvider.get(host,port)!=null){
                channel=ChannelProvider.get(host,port);
            }
            else {
                channel = bootstrap.connect(host, port).sync().channel();
                System.out.println("!");
                ChannelProvider.set(host,port,channel);
            }
            channel.writeAndFlush(rpcRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

//    public void close() throws Exception {
//        if(channel!=null)
//            channel.close();
//        group.shutdownGracefully().sync();
//    }
}
