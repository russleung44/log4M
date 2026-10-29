package com.tony.log4m;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Log4MApplication {

    public static void main(String[] args) {
        SpringApplication.run(Log4MApplication.class, args);
    }

}
