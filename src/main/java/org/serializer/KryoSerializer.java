package org.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.dto.RpcRequest;
import org.dto.RpcResponse;

public class KryoSerializer implements Serializer {
    private record KryoHolder(Kryo kryo, Output output, Input input) {

    }
    // 使用 ThreadLocal 保证每个线程都有自己的 Kryo 实例
    private final ThreadLocal<KryoHolder> kryoThreadLocal = ThreadLocal.withInitial(()-> {
        Kryo kryo = new Kryo();
        // 注册我们要传输的类，这样性能最高
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setRegistrationRequired(false);// 允许处理其他未注册类

        Output output = new Output(1024,-1);
        Input input=new Input();

        return new KryoHolder(kryo, output,input);
    });

    @Override
    public byte[] serialize(Object obj) {
       KryoHolder kryoHolder = kryoThreadLocal.get();
       kryoHolder.output().reset();
       kryoHolder.kryo().writeObject(kryoHolder.output, obj);
       return kryoHolder.output().toBytes();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        KryoHolder kryoHolder = kryoThreadLocal.get();
        kryoHolder.input().setBuffer(bytes);
        return kryoHolder.kryo().readObject(kryoHolder.input(), clazz);
    }

    @Override
    public byte getSerializerCode() {
        return SerializerTypeEnum.KRYO.getCode();
    }
}
