package org.ricky.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.tracing.TracingService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ricky.common.exception.ErrorCodeEnum.SYSTEM_ERROR;
import static org.ricky.common.exception.MyException.accessDeniedException;
import static org.ricky.common.exception.MyException.requestValidationException;
import static org.springframework.http.HttpStatus.valueOf;

@Slf4j
@Order(1)
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final List<Integer> WARN_CODES = List.of(400, 401, 403, 426, 429);
    private final TracingService tracingService;

    @ResponseBody
    @ExceptionHandler(MyException.class)
    public ResponseEntity<MyError> handleMryException(MyException ex, HttpServletRequest request) {
        if (WARN_CODES.contains(ex.getCode().getStatus())) {
            log.warn("Warning: {}", ex.getMessage());
        } else {
            log.error("Error: {}", ex.getMessage());
        }

        return createApiResult(ex, request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<MyError> handleAccessDinedException(HttpServletRequest request) {
        return createApiResult(accessDeniedException(), request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<MyError> handleInvalidRequest(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> error = ex.getBindingResult().getFieldErrors().stream()
                .collect(toImmutableMap(FieldError::getField, fieldError -> {
                    String message = fieldError.getDefaultMessage();
                    return isBlank(message) ? "无错误提示。" : message;
                }, (field1, field2) -> field1 + "|" + field2));

        log.error("Method argument validation Error[{}]: {}", ex.getParameter().getParameterType().getName(), error);
        MyException exception = requestValidationException(error);
        return createApiResult(exception, request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({ServletRequestBindingException.class, HttpMessageNotReadableException.class, ConstraintViolationException.class})
    public ResponseEntity<MyError> handleServletRequestBindingException(Exception ex, HttpServletRequest request) {
        MyException exception = requestValidationException("message", "请求验证失败。");
        log.error("Request processing Error: {}", ex.getMessage());
        return createApiResult(exception, request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<MyError> handleGeneralException(Throwable ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        String traceId = tracingService.currentTraceId();

        log.error("Error access[{}]:", path, ex);
        MyError error = new MyError(SYSTEM_ERROR, SYSTEM_ERROR.getStatus(), "系统错误。", path, traceId, null);
        return new ResponseEntity<>(error, new HttpHeaders(), valueOf(SYSTEM_ERROR.getStatus()));
    }

    private ResponseEntity<MyError> createApiResult(MyException exception, String path) {
        String traceId = tracingService.currentTraceId();
        MyError error = new MyError(exception, path, traceId);
        return new ResponseEntity<>(error, new HttpHeaders(), valueOf(error.getStatus()));
    }

}
