package com.rpc.serializer;

public interface Serializer {
    //对象->字节数组
    byte[] serialize(Object obj);

    //字节数组->对象
    <T> T deserialize(byte[] bytes, Class<T> clazz);

    //序列化器的编号 1代表Kryo
    byte getSerializerCode();
}
