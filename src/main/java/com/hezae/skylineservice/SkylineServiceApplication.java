package com.hezae.skylineservice;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@MapperScan(basePackages = {"com.hezae.skylineservice.mapper"})
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class SkylineServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkylineServiceApplication.class, args);
    }

}
