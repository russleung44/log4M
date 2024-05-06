package com.tony.log4m.exception;

import com.tony.log4m.base.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.telegram.telegrambots.longpolling.exceptions.TelegramApiErrorResponseException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.sql.SQLTransientException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * @author Tony
 * @since 2021/8/11
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 全局异常处理
     */
    @ExceptionHandler(Throwable.class)
    public R handleThrowable(Throwable e) {
        log.error("系统异常", e);
        return R.fail("系统异常");
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public R handleIndexOutOfBoundsException(IndexOutOfBoundsException e) {
        log.error("越界异常", e);
        return R.fail("系统异常");
    }

    /**
     * 运行异常处理
     */
    @ExceptionHandler(RuntimeException.class)
    public R handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error(request.getRequestURL().toString(), e);
        return R.fail(e.getMessage());
    }

    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e, HttpServletRequest request) {
        log.error(request.getRequestURL().toString(), e);
        return R.fail(e.getMessage());
    }


    /**
     * SQL异常处理
     */
    @ExceptionHandler(DataAccessException.class)
    public R handleException(DataAccessException e, HttpServletRequest request) {
        log.error(request.getRequestURL().toString(), e);
        return R.fail("查询错误");
    }

    /**
     * SQL异常处理
     */
    @ExceptionHandler(SQLException.class)
    public R handleException(SQLException e, HttpServletRequest request) {
        log.error(request.getRequestURL().toString(), e);
        return R.fail("查询错误");
    }



    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public R validatedBindException(BindException e, HttpServletRequest request) {
        List<FieldError> allErrors = e.getBindingResult().getFieldErrors();
        String msg = allErrors.stream().map(v -> v.getField() + " " + v.getDefaultMessage() + ", ").collect(Collectors.joining());
        log.error("BindException: {} {}", msg, request.getRequestURL());
        return R.fail(msg, false);
    }


    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public R illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage());
        return R.fail(e.getMessage(), false);
    }


    @ExceptionHandler(NoSuchElementException.class)
    public R noSuchElementExceptionHandler() {
        return R.fail("数据不存在");
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException: {}", e.getMessage());
        return R.fail("请求参数不合法", false);
    }


    @ExceptionHandler(SQLTransientException.class)
    public R mySQLTransactionRollbackExceptionHandler(SQLTransientException e) {
        log.error("SQLTransientException", e);
        return R.fail("SQL事务异常", false);
    }

    @ExceptionHandler(TelegramApiErrorResponseException.class)
    public void telegramApiErrorResponseExceptionHandler(TelegramApiErrorResponseException e) {
        log.error("TelegramApiErrorResponseException: {}", e.getMessage());
    }

}
