package com.wistron.javacodebase.common.exception;

import com.wistron.javacodebase.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        log.warn("業務異常: {}", e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("系統異常: ", e);
        return ResponseEntity.internalServerError().body(ApiResponse.failure("INTERNAL_SERVER_ERROR"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(404).body(ApiResponse.failure("NOT_FOUND"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(405).body(ApiResponse.failure("METHOD_NOT_ALLOWED"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        log.warn("參數驗證失敗: {}", message);
        return ResponseEntity.badRequest().body(ApiResponse.failure(message));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        String message = e.getParameterValidationResults().stream()
            .flatMap(validation -> validation.getResolvableErrors().stream())
            .map(error -> error.getCodes()[0] + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        log.warn("參數驗證失敗: {}", message);
        return ResponseEntity.badRequest().body(ApiResponse.failure(message));
    }
}
