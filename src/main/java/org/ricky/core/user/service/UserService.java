package org.ricky.core.user.service;

import org.ricky.common.result.ApiResult;
import org.ricky.core.user.domain.dto.RegistryUserDTO;
import org.ricky.core.user.domain.dto.UserDTO;
import org.ricky.core.user.domain.dto.UserLoginDTO;
import org.ricky.core.user.domain.vo.UserLoginVO;
import org.ricky.core.user.domain.vo.UserVO;

import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className UserService
 * @desc
 */
public interface UserService {
    ApiResult<String> registry(RegistryUserDTO userDTO);

    ApiResult<String> registryBatch(List<RegistryUserDTO> userDTOS);

    ApiResult<UserLoginVO> login(UserLoginDTO userDTO);

    ApiResult<UserVO> getById(String userId);

    ApiResult<UserVO> getByUsername(String username);

    ApiResult<String> updateById(UserDTO userDTO);
}
