package com.tony.log4m.aop;

import com.tony.log4m.utils.AopUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author Tony
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CustomAop {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RestController)")
    public void api() {
    }

    @AfterThrowing(throwing = "ex", pointcut = "api()")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("=========异常输出()=========");
        AopUtil.exceptionLog(joinPoint, ex);
    }

}