package org.ricky.core.user.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.DTO;
import org.ricky.core.common.validation.password.Password;

import static org.ricky.common.constants.ConfigConstant.MAX_GENERIC_NAME_LENGTH;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className RegistryUserDTO
 * @desc 用户注册对象
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistryUserDTO implements DTO {

    @NotBlank
    @Size(max = MAX_GENERIC_NAME_LENGTH)
    String username;

    @NotBlank
    @Password
    String password;

}
