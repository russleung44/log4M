package com.tony.log4m.exception;

/**
 * 自定义异常
 */
public class Log4mException extends RuntimeException {

    public Log4mException(String message) {
        super(message);
    }

    public Log4mException(String message, Throwable cause) {
        super(message, cause);
    }

    public Log4mException(Throwable cause) {
        super(cause);
    }

    public Log4mException() {
        super();
    }

}
