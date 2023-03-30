package com.tony.log4m.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author TonyLeung
 * @since 2020/6/22
 */
@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        // 放行哪些原始域
        config.addAllowedOrigin("*");
        // 是否发送Cookie信息
        config.setAllowCredentials(false);
        // 放行哪些原始域(请求方式)
        config.addAllowedMethod("*");
        // 放行哪些原始域(头部信息)
        config.addAllowedHeader("*");
        // 暴露哪些头部信息
        config.addExposedHeader("*");

        // 添加映射路径
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        return new CorsFilter(configSource);
    }

}
