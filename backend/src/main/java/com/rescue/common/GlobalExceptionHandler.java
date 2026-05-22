package com.rescue.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<?> handle(Exception e) {
        e.printStackTrace();
        return Result.error(e.getMessage() == null ? "服务器异常" : e.getMessage());
    }
}
