package org.ricky.common.result;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className SuccessApiResult
 * @desc 成功返回体
 */
@Getter
@Setter
public final class SuccessApiResult<T> extends BaseApiResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private T data;

    public SuccessApiResult() {
        this(null);
    }

    public SuccessApiResult(T data) {
        this.timestamp = System.currentTimeMillis();
        this.code = 200;
        this.data = data;
    }

}
