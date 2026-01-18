package com.rpc.serializer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SerializerTypeEnum {
    // 定义支持的序列化协议
    KRYO((byte)1),
    PROTOBUF((byte)2),
    HESSIAN((byte)3);

    private final byte code;

    public byte code(){
        return code;
    }
}
