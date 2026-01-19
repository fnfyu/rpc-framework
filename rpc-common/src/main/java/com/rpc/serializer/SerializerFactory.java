package com.rpc.serializer;

import com.rpc.config.RpcConfig;
import com.rpc.extension.ExtensionLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializerFactory {
    private static final Map<Byte,Serializer> SERIALIZER_MAP = new HashMap<>();

    static {
        Map<String,Serializer> instances=ExtensionLoader.getAllExtensions(Serializer.class);
        instances.forEach((name,serializer)->{
            SERIALIZER_MAP.put(serializer.getSerializerCode(),serializer);
            System.out.println(">>> [工厂自动化] 发现并注册序列化器: " + name + " (Code: " + serializer.getSerializerCode() + ")");
        });
    }

    public static Serializer getSerialierByCode(Byte code) {
        Serializer serializer =SERIALIZER_MAP.get(code);
        if (serializer == null){
            throw new RuntimeException("未知的序列化类型");
        }
        return serializer;
    }

    public static Serializer getSerializer(){
        return ExtensionLoader.getExtension(Serializer.class,RpcConfig.serializerName);
//        switch (RpcConfig.serializerCode){
//            case 2:
//                return new ProtoSerializer();
//            case 3:
//                return new JsonSerializer();
//            case 1:
//            default:
//                return new KryoSerializer();
//        }
    }
}
