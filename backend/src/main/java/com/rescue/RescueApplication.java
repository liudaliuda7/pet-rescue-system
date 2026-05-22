package com.rescue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rescue.mapper")
public class RescueApplication {
    public static void main(String[] args) {
        SpringApplication.run(RescueApplication.class, args);
    }
}
