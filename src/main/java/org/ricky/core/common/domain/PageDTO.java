package org.ricky.core.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static org.ricky.common.constants.MessageConstants.PARAMS_FORMAT_ERROR;
import static org.ricky.common.constants.MessageConstants.PARAMS_IS_NOT_NULL;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/12
 * @className PageDTO
 * @desc 分页查询入参
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageDTO implements DTO {

    /**
     * 页号
     */
    @NotNull(message = PARAMS_IS_NOT_NULL)
    @Min(value = 1, message = PARAMS_FORMAT_ERROR)
    @Schema(description = "分页查询的页数，从1开始", requiredMode = REQUIRED)
    protected Integer pageNum;

    /**
     * 每页条数
     */
    @NotNull(message = PARAMS_IS_NOT_NULL)
    @Min(value = 1, message = PARAMS_FORMAT_ERROR)
    @Max(value = 100, message = PARAMS_FORMAT_ERROR)
    @Schema(description = "每页查询的条数，范围是1到100", requiredMode = REQUIRED)
    protected Integer pageSize;

}
