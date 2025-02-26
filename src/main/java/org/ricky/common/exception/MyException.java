package org.ricky.common.exception;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/9
 * @className MyException
 * @desc 项目基本异常
 */
@Getter
public class MyException extends RuntimeException {

    /**
     * 错误码
     */
    private final ErrorCodeEnum code;

    /**
     * 异常数据
     * key=数据名 value=数据
     */
    private final Map<String, Object> data = Maps.newHashMap();

    /**
     * 使用者提供的异常信息
     */
    private final String userMessage;

    /**
     * 格式化后的异常信息
     */
    private String message;

    public MyException(ErrorCodeEnum code, String userMessage) {
        this.code = code;
        this.userMessage = userMessage;
        this.message = message(userMessage);
    }

    public MyException(ErrorCodeEnum code, String userMessage,
                       String key, Object value) {
        this.code = code;
        addData(key, value);
        this.userMessage = userMessage;
        this.message = message(userMessage);
    }

    public MyException(ErrorCodeEnum code, String userMessage,
                       String key1, Object value1,
                       String key2, Object value2) {
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        this.userMessage = userMessage;
        this.message = message(userMessage);
    }

    public MyException(ErrorCodeEnum code, String userMessage,
                       String key1, Object value1,
                       String key2, Object value2,
                       String key3, Object value3) {
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        addData(key3, value3);
        this.userMessage = userMessage;
        this.message = message(userMessage);
    }

    public MyException(ErrorCodeEnum code, String userMessage,
                       String key1, Object value1,
                       String key2, Object value2,
                       String key3, Object value3,
                       String key4, Object value4) {
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        addData(key3, value3);
        addData(key4, value4);
        this.userMessage = userMessage;
        this.message = message(userMessage);
    }

    public MyException(ErrorCodeEnum code, String userMessage, Map<String, Object> data) {
        this.code = code;
        this.data.putAll(data);
        this.userMessage = userMessage;
        this.message = message(userMessage);
    }

    /**
     * 格式化异常信息
     *
     * @param userMessage 使用者提供的异常信息
     * @return 异常信息
     */
    private String message(String userMessage) {
        StringBuilder stringBuilder = new StringBuilder().append("[").append(this.code.toString()).append("]");

        if (isNotBlank(userMessage)) {
            stringBuilder.append(userMessage);
        }

        if (isNotEmpty(this.data)) {
            stringBuilder.append("Data: ").append(this.data);
        }

        return stringBuilder.toString();
    }

    /**
     * 抛出校验异常
     *
     * @param data 异常数据
     * @return 校验异常
     */
    public static MyException requestValidationException(Map<String, Object> data) {
        return new MyException(ErrorCodeEnum.REQUEST_VALIDATION_FAILED, "请求数据验证失败。", data);
    }

    /**
     * 抛出校验异常
     *
     * @param key   数据名
     * @param value 数据值
     * @return 校验异常
     */
    public static MyException requestValidationException(String key, Object value) {
        return new MyException(ErrorCodeEnum.REQUEST_VALIDATION_FAILED, "请求数据验证失败。", key, value);
    }

    /**
     * 抛出校验异常
     *
     * @param message 异常信息
     * @return 校验异常
     */
    public static MyException requestValidationException(String message) {
        return new MyException(ErrorCodeEnum.REQUEST_VALIDATION_FAILED, message);
    }

    /**
     * 抛出拒绝访问异常
     *
     * @return 拒绝访问异常
     */
    public static MyException accessDeniedException() {
        return new MyException(ErrorCodeEnum.ACCESS_DENIED, "权限不足。");
    }

    /**
     * 抛出拒绝访问异常
     *
     * @param userMessage 使用者提供的异常信息
     * @return 拒绝访问异常
     */
    public static MyException accessDeniedException(String userMessage) {
        return new MyException(ErrorCodeEnum.ACCESS_DENIED, userMessage);
    }

    /**
     * 抛出认证异常
     *
     * @return 认证异常
     */
    public static MyException authenticationException() {
        return new MyException(ErrorCodeEnum.AUTHENTICATION_FAILED, "登录失败。");
    }

    /**
     * 添加异常数据
     *
     * @param key   数据名
     * @param value 数据值
     */
    public void addData(String key, Object value) {
        this.data.put(key, value);
        this.message = message(this.userMessage);
    }

}
