package org.ricky.core.user.service;

import org.ricky.common.result.ApiResult;
import org.ricky.core.user.domain.dto.RegistryUserDTO;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className UserService
 * @desc
 */
public interface UserService {
    ApiResult<String> registry(RegistryUserDTO userDTO);
}
