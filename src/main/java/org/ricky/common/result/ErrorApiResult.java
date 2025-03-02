package org.ricky.common.result;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className ErrorApiResult
 * @desc 失败返回体
 */
@Deprecated(forRemoval = true)
public final class ErrorApiResult extends BaseApiResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误信息
     */
    public String message;

    public ErrorApiResult(Integer code, String message) {
        this.timestamp = System.currentTimeMillis();
        this.code = code;
        this.message = message;
    }

}
