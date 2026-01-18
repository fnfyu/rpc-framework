package com.rpc.client;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRequests{
    private static final Map<String, CompletableFuture<Object>>CACHE = new ConcurrentHashMap<>();

    public static void put(String requestId, CompletableFuture<Object> future){
        CACHE.put(requestId, future);
    }

    public static void complete(String requestId, Object result){
        CompletableFuture<Object> future = CACHE.remove(requestId);
        if (future != null){
            future.complete(result);
        }
    }

    public static void remove(String requestId){
        CACHE.remove(requestId);
    }
}
