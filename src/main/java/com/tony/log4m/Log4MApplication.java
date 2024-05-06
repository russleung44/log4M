package com.tony.log4m;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({"com.tony.log4m.dao"})
@SpringBootApplication
public class Log4MApplication {

    public static void main(String[] args) {
        SpringApplication.run(Log4MApplication.class, args);
    }

}
