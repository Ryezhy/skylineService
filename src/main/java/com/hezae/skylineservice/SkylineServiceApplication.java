package com.hezae.skylineservice;
import cn.dev33.satoken.SaManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@MapperScan(basePackages = {"com.hezae.skylineservice.mapper"})
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})

public class SkylineServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkylineServiceApplication.class, args);
        //System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());

    }

}
