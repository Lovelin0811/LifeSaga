package com.lovelin.lifesaga.shared.interfaces.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> handleBusinessException(RuntimeException exception) {
        String message = exception.getMessage() == null ? "请求处理失败" : exception.getMessage();
        HttpStatus status = resolveStatus(exception, message);
        return ResponseEntity.status(status)
                .body(errorBody(status, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception exception) {
        log.error("Unhandled exception", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误"));
    }

    private HttpStatus resolveStatus(RuntimeException exception, String message) {
        if (exception instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }
        if ("未登录".equals(message) || message.contains("token")) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (message.contains("无权")) {
            return HttpStatus.FORBIDDEN;
        }
        if (message.contains("不存在")) {
            return HttpStatus.NOT_FOUND;
        }
        if (message.contains("并发") || message.contains("已被修改")) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.BAD_REQUEST;
    }

    private Map<String, Object> errorBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", status.value());
        body.put("data", null);
        body.put("message", message);
        return body;
    }
}
