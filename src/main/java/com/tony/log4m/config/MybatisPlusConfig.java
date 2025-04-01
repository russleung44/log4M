package com.tony.log4m.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Tony
 * @since 4/1/2025
 */
@Configuration
@MapperScan("com.tony.log4m.mapper")
public class MybatisPlusConfig {
}
