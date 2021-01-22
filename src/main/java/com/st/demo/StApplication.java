package com.st.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.st.demo.mapper")
public class StApplication {

    public static void main(String[] args) {
        SpringApplication.run(StApplication.class, args);
    }

}
