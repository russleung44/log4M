package com.tony.log4m.exception;

import com.tony.log4m.models.vo.ResultVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 *
 * @author Tony
 * @since 2021/8/11
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Log4mException.class)
    public ResponseEntity<ResultVO<?>> handleLog4mException(Log4mException e, HttpServletRequest request) {
        log.warn("Log4mException: [{}] {}", request.getRequestURL(), e.getMessage());
        return ResponseEntity.badRequest().body(ResultVO.error(e.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleThrowable(Throwable e, HttpServletRequest request) {
        log.error(request.getRequestURL().toString(), e);
        return ResponseEntity.internalServerError().body("系统异常");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error(request.getRequestURL().toString(), e);
        return ResponseEntity.internalServerError().body("系统异常");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e, HttpServletRequest request) {
        log.error(request.getRequestURL().toString(), e);
        return ResponseEntity.internalServerError().body("系统异常");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // 提取验证错误信息
        final BindingResult bindingResult = ex.getBindingResult();
        final List<String> errorMessages = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        // 记录日志（可自定义日志级别）
        log.warn("参数校验失败: {}, URL: {}", errorMessages, request.getRequestURL());

        // 返回400 Bad Request响应
        return ResponseEntity
                .badRequest()
                .body("参数校验失败: " + String.join(", ", errorMessages));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException e, HttpServletRequest request) {
        String msg = e.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("BindException: [{}] {}", request.getRequestURL(), msg);
        return ResponseEntity.badRequest().body(msg);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String errorMessage = String.format(
                "参数转换错误：参数 '%s' 应为 %s 类型，但值 '%s' 无法被转换。",
                e.getName(),
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知类型",
                e.getValue() != null ? e.getValue() : "null"
        );
        log.warn("TypeMismatchException: [{}] {}", request.getRequestURL(), errorMessage);
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException ex) {
        if (ex.getMessage().contains("favicon.ico")) {
            // 忽略 favicon.ico 请求
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


}
