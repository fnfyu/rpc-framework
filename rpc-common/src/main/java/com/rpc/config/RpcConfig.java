package com.rpc.config;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RpcConfig {
    private static final Properties properties;
    public static String zkAddress="127.0.0.1:2181";
    public static int retryTimes=3;
    public static String serializerName="kryo";
    public static String loadBalance="random";
    public static int port= 12345;

    static {
        properties = new Properties();
        try {
            InputStream is = RpcConfig.class.getClassLoader().getResourceAsStream("rpc.properties");
            if (is != null) {
                properties.load(is);
                zkAddress = properties.getProperty("rpc.zookeeper.address",zkAddress);
                retryTimes=Integer.parseInt(properties.getProperty("rpc.retry.times",String.valueOf(retryTimes)));
                serializerName=properties.getProperty("rpc.serializer.name",serializerName);
                loadBalance=properties.getProperty("rpc.loadbalance",loadBalance);
                port=Integer.parseInt(properties.getProperty("rpc.port",String.valueOf(port)));
                System.out.println("[配置加载] 成功读取 rpc.properties");
            }
            else {
                System.out.println("[警告] 未找到rpc.properties,使用默认配置");
            }
        } catch (IOException e) {
            System.out.println(">>>[错误] 加载配置文件失败 :"+e.getMessage());
        }
    }
}
