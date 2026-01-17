package org.serializer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerTypeEnum {
    // 定义支持的序列化协议
    KRYO((byte) 1, "kryo"),
    PROTOBUF((byte) 2, "protobuf"),
    HESSIAN((byte) 3, "hessian");

    private final byte code;
    private final String name;

    public static String getName(byte code){
        for (SerializerTypeEnum serializerTypeEnum : SerializerTypeEnum.values()) {
            if (serializerTypeEnum.getCode() == code) {
                return serializerTypeEnum.getName();
            }
        }
        return null;
    }
}
