package org.ricky.core.user.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.common.domain.DTO;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static org.ricky.common.constants.MessageConstants.*;
import static org.ricky.common.constants.RegexConstant.NUM_WORD_REG;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/9
 * @className UserLoginDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLoginDTO implements DTO {

    @NotNull(message = PARAMS_IS_NOT_NULL)
    @Size(min = 3, max = 32, message = PARAMS_LENGTH_REQUIRED)
    @Pattern(regexp = NUM_WORD_REG, message = PARAMS_FORMAT_ERROR)
    @Schema(description = "用户名，最小3个字符，最长32个字符", requiredMode = REQUIRED)
    String username;

    @NotNull(message = PARAMS_IS_NOT_NULL)
    @Size(min = 3, max = 32, message = PARAMS_LENGTH_REQUIRED)
    @Pattern(regexp = NUM_WORD_REG, message = PARAMS_FORMAT_ERROR)
    @Schema(description = "用户密码，最小3个字符， 最长32个字符", requiredMode = REQUIRED)
    String password;

}
