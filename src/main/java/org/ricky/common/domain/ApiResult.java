package org.ricky.common.domain;

import lombok.Getter;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/2
 * @className ApiResult
 * @desc 通用返回体 <br>
 * 规范：<br>
 * 1. 其静态方法只允许在controller中调用<br>
 * 2. api接口中所有异常只允许使用MyException抛出<br>
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
     * 创建成功返回体
     * 包含true，表示操作成功
     *
     * @return ApiResult
     */
    public static ApiResult<Boolean> success() {
        return success(true);
    }

    /**
     * 创建操作失败返回体，在不需要抛出异常的场景下使用
     * 包含false，表示操作失败
     *
     * @return ApiResult
     */
    public static ApiResult<Boolean> fail() {
        return success(false);
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

}
