package com.programmer.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UtilApplication {

    public static void main(String[] args) {
        SpringApplication.run(UtilApplication.class, args);
    }

}
