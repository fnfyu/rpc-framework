package com.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonSerializer implements  Serializer {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public byte[] serialize(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return mapper.readValue(bytes,clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte getSerializerCode() {
        return SerializerTypeEnum.JSON.code();
    }
}
