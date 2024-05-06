package com.tony.log4m.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author TonyLeung
 * @since 2022/9/6
 */
@Slf4j
public class AopUtil {

    public static void exceptionLog(JoinPoint joinPoint, Throwable ex) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        // 请求方式
        String method = request.getMethod();
        // URL
        String url = request.getRequestURL().toString();
        // 请求参数
        StringBuilder params = AopUtil.getParams(joinPoint);
        // 切点签名
        Signature signature = joinPoint.getSignature();
        // 类名
        String className = joinPoint.getTarget().getClass().getName();
        // 方法名
        String methodName = signature.getName();

        log.error("异常请求:[ {} {} ]", url, method);
        log.error("异常请求方法:{}", className + "." + methodName);
        log.error("异常请求参数:{}", params);

        if (ex instanceof RuntimeException) {
            log.error("异常信息:{}", ex.getMessage());
        } else {
            log.error("异常信息", ex);
        }
    }


    public static StringBuilder getParams(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder params = new StringBuilder();
        for (Object object : args) {
            if (object instanceof HttpServletRequest || object instanceof HttpServletResponse || object instanceof MultipartFile) {
                continue;
            }
            try {
                params.append(JacksonUtil.toStr(object)).append(" ");
            } catch (Exception e) {
                log.error("参数拼接异常:", e);
            }
        }
        return params;
    }
}
