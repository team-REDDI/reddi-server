package com.example.reddiserver.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleExceptions(Exception exception) {

        log.error("Exception occurred:", exception); // 스택 트레이스 정보를 포함한 로깅

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.errorResponse(exception.getMessage()));
    }
}
