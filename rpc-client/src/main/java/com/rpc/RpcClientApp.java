package com.rpc;

import com.rpc.service.TestService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcClientApp implements CommandLineRunner {
    private TestService testService;

    public RpcClientApp(TestService testService) {
        this.testService = testService;
    }

    public static void main(String[] args) {
        SpringApplication.run(RpcClientApp.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        testService.test();
    }
}
