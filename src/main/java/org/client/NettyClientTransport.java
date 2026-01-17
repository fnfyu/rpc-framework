package org.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.dto.RpcRequest;
import org.dto.RpcResponse;
import org.serializer.KryoSerializer;
import org.serializer.MyRpcDecoder;
import org.serializer.MyRpcEncoder;

public class NettyClientTransport {
    private static final EventLoopGroup group = new NioEventLoopGroup();
    private static final Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private String host;
    private int port;

    public NettyClientTransport(String host, int port) {
        this.host = host;
        this.port = port;
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) throws InterruptedException {
                channel.pipeline().addLast(new MyRpcEncoder(new KryoSerializer()));
                channel.pipeline().addLast(new MyRpcDecoder(new KryoSerializer(),RpcResponse.class));
                channel.pipeline().addLast(new SimpleChannelInboundHandler<RpcResponse>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
                        System.out.println("[客户端收到执行结果]"+rpcResponse);
                        UnprocessedRequests.complete(rpcResponse.getRequestId(), rpcResponse.getResult());
                    }
                });
            }
        });
    }


    public void send(RpcRequest rpcRequest) throws Exception {
        if (channel==null || !channel.isActive()) {
            channel = bootstrap.connect(host, port).sync().channel();
            //System.out.println("建立连接");
        }

        channel.writeAndFlush(rpcRequest);

    }

    public void close() throws Exception {
        if(channel!=null)
            channel.close();
        group.shutdownGracefully().sync();
    }
}
