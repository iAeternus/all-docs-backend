package org.ricky.core.user.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.common.auth.PermissionEnum;
import org.ricky.common.domain.DTO;
import org.ricky.core.common.validation.id.Id;

import static org.ricky.common.constants.ConfigConstant.USER_ID_PREFIX;
import static org.ricky.common.constants.MessageConstants.PARAMS_IS_NOT_NULL;

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

    @Id(pre = USER_ID_PREFIX)
    @NotNull(message = PARAMS_IS_NOT_NULL)
    String userId;

    @NotNull(message = PARAMS_IS_NOT_NULL)
    PermissionEnum newRole;

}
