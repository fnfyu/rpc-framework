package com.rpc.serializer;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {
    private static final Map<Byte,Serializer> SERIALIZER_MAP = new HashMap<>();

    static {
        Serializer kryoSerializer = new KryoSerializer();
        SERIALIZER_MAP.put(kryoSerializer.getSerializerCode(), kryoSerializer);
    }

    public static Serializer getSerialierByCode(Byte code) {
        Serializer serializer =SERIALIZER_MAP.get(code);
        if (serializer == null){
            throw new RuntimeException("未知的序列化类型");
        }
        return serializer;
    }
}
