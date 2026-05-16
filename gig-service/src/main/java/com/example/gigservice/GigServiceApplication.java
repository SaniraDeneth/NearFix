package com.example.gigservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GigServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GigServiceApplication.class, args);
    }

}
