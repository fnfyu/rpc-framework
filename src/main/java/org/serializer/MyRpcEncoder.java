package org.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

//把 Netty 的 ObjectEncoder 换成我们自己的 MyRpcEncoder
public class MyRpcEncoder extends MessageToByteEncoder<Object> {
    private final Serializer serializer;

    public MyRpcEncoder(Serializer serializer) {
        this.serializer=serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        // 1. 写入魔数 (4字节)
        byteBuf.writeInt(0xCCBCCBCB);
        // 2. 写入版本 (1字节)
        byteBuf.writeByte(1);
        // 3. 写入序列化方式 (1字节)
        byteBuf.writeByte(serializer.getSerializerCode());
        // 4. 序列化数据
        System.out.println(o);
        byte[] body=serializer.serialize(o);
        //System.out.println(body.length);

        // 5. 写入数据长度 (4字节)
        byteBuf.writeInt(body.length);
        // 6. 写入实际数据
        byteBuf.writeBytes(body);
    }
}
