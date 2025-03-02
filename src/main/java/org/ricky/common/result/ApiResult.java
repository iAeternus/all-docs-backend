package org.ricky.common.result;

import lombok.Getter;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/2
 * @className ApiResult
 * @desc 通用返回体
 */
@Value
@Getter
public class ApiResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前时间戳
     */
    Long timestamp;

    /**
     * 状态码
     * 200-成功，其他-失败
     */
    Integer code;

    /**
     * 返回数据
     */
    T data;

    private ApiResult(Long timestamp, Integer code, T data) {
        this.timestamp = timestamp;
        this.code = code;
        this.data = data;
    }

    /**
     * 创建成功返回体，无数据
     *
     * @return ApiResult
     */
    public static <T> ApiResult<T> success() {
        return success(null);
    }

    /**
     * 创建成功返回体，包含数据
     *
     * @param data 数据体
     * @return ApiResult
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(System.currentTimeMillis(), 200, data);
    }

    /**
     * 创建错误返回体
     *
     * @param code    错误码
     * @param message 错误信息
     * @return ApiResult
     */
    public static ApiResult<String> error(Integer code, String message) {
        return new ApiResult<>(System.currentTimeMillis(), code, message);
    }

}
