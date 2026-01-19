package com.rpc.extension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionLoader {
    private static final String SERVICE_DIRECTORY = "META-INF/rpc/";
    // 缓存：接口类型 -> (别名 -> 实例对象)
    private static final Map<Class<?>,Map<String,Object>> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getExtension(Class<T>clazz,String name){
        Map<String,Object> instances = EXTENSION_INSTANCES.computeIfAbsent(clazz,k->new ConcurrentHashMap<>());
        if (instances.containsKey(name)){
            return (T)instances.get(name);
        }
        // 2. 没缓存，去读 resources/META-INF/rpc/ 接口全名 的文件
        // 比如：META-INF/rpc/com.rpc.serializer.Serializer
        String fileName=SERVICE_DIRECTORY+clazz.getName();
        URL url=ExtensionLoader.class.getClassLoader().getResource(fileName);

        try(BufferedReader reader=new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while((line=reader.readLine())!=null){
                String []parts=line.split("=");
                if(parts.length==2&&parts[0].trim().equals(name)){
                    // 3. 找到匹配的别名，反射实例化
                    Class<?> instanceClass=Class.forName(parts[1].trim());
                    T instance=(T)instanceClass.getDeclaredConstructor().newInstance();
                    instances.put(name,instance);
                    System.out.println(instance.getClass().getSimpleName());
                    return instance;
                }
            }

        } catch (Exception e) {
            System.err.println(">>> [SPI错误] 加载插件失败: " + e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String,T> getAllExtensions(Class<T>clazz){
        String fileName=SERVICE_DIRECTORY+clazz.getName();
        URL url=ExtensionLoader.class.getClassLoader().getResource(fileName);
        Map<String,T> instances=new ConcurrentHashMap<>();

        try(BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(url.openStream()))){
            Properties properties=new Properties();
            properties.load(bufferedReader);

            for(String propertyName:properties.stringPropertyNames()){
                instances.put(propertyName,getExtension(clazz,propertyName));
            }

            return instances;
        } catch (Exception e) {
            System.err.println(">>> [SPI错误] 加载插件失败: " + e.getMessage());
        }
        return null;
    }
}
