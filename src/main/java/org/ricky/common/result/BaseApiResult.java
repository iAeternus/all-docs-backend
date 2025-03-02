package org.ricky.common.result;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className BaseApiResult
 * @desc 通用返回体
 */
@Deprecated
public abstract class BaseApiResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前时间戳
     */
    public Long timestamp;

    /**
     * 状态码
     * 200-成功，其他-失败
     */
    @Getter
    public Integer code;

    /**
     * 创建成功返回体，无数据
     *
     * @return BaseApiResult
     */
    public static BaseApiResult success() {
        return new SuccessApiResult<>();
    }

    /**
     * 创建成功返回体，包含数据
     *
     * @param data 数据体
     * @return BaseApiResult
     */
    public static <T> BaseApiResult success(T data) {
        return new SuccessApiResult<>(data);
    }

    /**
     * 创建错误返回体
     *
     * @param code    错误码
     * @param message 错误信息
     * @return BaseApiResult
     */
    public static BaseApiResult error(Integer code, String message) {
        return new ErrorApiResult(code, message);
    }

}
