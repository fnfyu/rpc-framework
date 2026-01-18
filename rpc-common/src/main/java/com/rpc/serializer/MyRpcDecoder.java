package com.rpc.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

//网络传输中，数据可能是一点点传过来的。解码器要负责把碎片拼成完整的包裹。

public class MyRpcDecoder extends ByteToMessageDecoder {
    private Serializer serializer;
    private Class<?> clazz;

    public MyRpcDecoder(Class<?> clazz) {
        this.clazz=clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 1. 至少得有魔数+版本+类型+长度 = 10字节，才值得读
        if (byteBuf.readableBytes() < 10) {
            return;
        }

        byteBuf.markReaderIndex(); // 标记当前位置，如果数据不够读，可以撤回

        int magic= byteBuf.readInt();
        if (magic!=0xCCBCCBCB){
            throw new RuntimeException("非法协议");
        }

        byte version= byteBuf.readByte();
        //System.out.println(version);
        byte serializerType= byteBuf.readByte();
        serializer=SerializerFactory.getSerialierByCode(serializerType);
        //System.out.println(serializerType);
        int length= byteBuf.readInt();
        //System.out.println(length);

        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();//数据还还没有传完，撤回指针，下次再读
            return;
        }

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        // 根据 serializeType 找到对应的序列化器并还原对象...
        // 这里暂时假设咱们收到的就是 RpcRequest 或 RpcResponse
        Object object=serializer.deserialize(bytes,clazz);// 你的序列化器.deserialize(body, ...);
       // System.out.println(object);
        list.add(object);


    }
}
