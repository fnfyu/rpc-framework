package com.rpc.serializer;

public class ProtoSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }

    @Override
    public byte getSerializerCode() {
        return SerializerTypeEnum.PROTOBUF.code();
    }
}
