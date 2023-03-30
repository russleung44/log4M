package com.tony.log4m.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tony.log4m.base.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.validation.constraints.NotNull;

/**
 * @author TonyLeung
 * @since 2022/3/22
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.tony.log4m.controller")
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 是否开启功能 true:开启
     */
    @Override
    public boolean supports(@NotNull MethodParameter methodParameter, @NotNull Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    /**
     * 处理返回结果
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (body == null) {
            return R.ok();
        }

        if (body instanceof String) {
            try {
                return new ObjectMapper().writeValueAsString(R.ok(body));
            } catch (JsonProcessingException e) {
                e.printStackTrace();

            }
        }

        if (body instanceof R) {
            return body;
        }
        return R.ok(body);
    }
}

