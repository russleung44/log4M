package com.tony.log4m.config;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tony
 */
@Configuration
@EnableSwagger2
public class Knife4jWebConfig {

    @Bean
    public Docket defaultApi() {
        List<RequestParameter> pars = new ArrayList<>();

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(pars)
                .apiInfo(new ApiInfoBuilder()
                        .version("0.0.1")
                        .title("log4M")
                        .description("log4M")
                        .build());
    }
}