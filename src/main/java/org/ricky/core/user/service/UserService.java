package org.ricky.core.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ricky.common.domain.PageDTO;
import org.ricky.common.domain.PageVO;
import org.ricky.common.domain.ApiResult;
import org.ricky.core.user.domain.dto.*;
import org.ricky.core.user.domain.vo.UserLoginVO;
import org.ricky.core.user.domain.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

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

    ApiResult<Boolean> registryBatch(List<RegistryUserDTO> userDTOS);

    ApiResult<UserLoginVO> login(UserLoginDTO userDTO);

    ApiResult<UserVO> getById(String userId);

    ApiResult<UserVO> getByUsername(String username);

    ApiResult<Boolean> updateById(UserDTO userDTO);

    ApiResult<Boolean> deleteById(String userId);

    ApiResult<Boolean> deleteByIdBatch(DeleteByIdBatchDTO dto);

    ApiResult<Boolean> checkLoginState(HttpServletRequest request, HttpServletResponse response);

    ApiResult<PageVO<UserVO>> page(PageDTO pageDTO);

    ApiResult<Boolean> updateRole(UpdateRoleDTO dto);

    ApiResult<Boolean> deactivate(String userId);

    ApiResult<Boolean> activate(String userId);

    ApiResult<Boolean> uploadAvatar(MultipartFile img);

    ApiResult<Boolean> deleteAvatar();

    ApiResult<Boolean> resetPwd(ResetPwdDTO dto);
}
