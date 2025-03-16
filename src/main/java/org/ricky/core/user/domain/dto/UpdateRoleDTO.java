package org.ricky.core.user.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.common.auth.PermissionEnum;
import org.ricky.core.common.domain.DTO;
import org.ricky.core.common.validation.id.Id;

import static org.ricky.common.constants.ConfigConstant.USER_ID_PREFIX;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/12
 * @className UpdateRoleDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateRoleDTO implements DTO {

    @NotBlank
    @Id(pre = USER_ID_PREFIX)
    String userId;

    @NotNull
    PermissionEnum newRole;

}
