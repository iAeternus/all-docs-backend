package org.ricky.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

import static java.time.Instant.now;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/25
 * @className MyError
 * @desc 错误
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MyError implements Serializable {

    /**
     * 错误码
     */
    ErrorCodeEnum code;

    /**
     * 错误信息
     */
    String message;

    /**
     * 用户信息
     */
    String userMessage;

    /**
     * 响应状态
     */
    int status;

    /**
     * url路径
     */
    String path;

    /**
     * 时间戳
     */
    Instant timestamp;

    /**
     * 追踪ID
     */
    String traceId;

    /**
     * 相关数据
     */
    Map<String, Object> data;

    public MyError(MyException ex, String path, String traceId) {
        ErrorCodeEnum errorCode = ex.getCode();
        this.code = errorCode;
        this.message = ex.getMessage();
        this.userMessage = ex.getUserMessage();
        this.status = errorCode.getStatus();
        this.path = path;
        this.timestamp = now();
        this.traceId = traceId;
        this.data = ex.getData();
    }

    public MyError(ErrorCodeEnum code, int status, String message, String path, String traceId, Map<String, Object> data) {
        this.code = code;
        this.message = message;
        this.userMessage = message;
        this.status = status;
        this.path = path;
        this.timestamp = now();
        this.traceId = traceId;
        this.data = data;
    }

}
