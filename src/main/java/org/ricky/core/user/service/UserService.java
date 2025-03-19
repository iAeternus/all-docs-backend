package org.ricky.core.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ricky.core.common.domain.ApiResult;
import org.ricky.core.common.domain.PageDTO;
import org.ricky.core.common.domain.PageVO;
import org.ricky.core.user.domain.dto.*;
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
    String registry(RegistryUserDTO userDTO);

    Boolean registryBatch(List<RegistryUserDTO> userDTOS);

    UserLoginVO login(UserLoginDTO userDTO);

    UserVO getById(String userId);

    UserVO getByUsername(String username);

    Boolean updateById(UserDTO userDTO);

    Boolean deleteById(String userId);

    Boolean deleteByIdBatch(DeleteByIdBatchDTO dto);

    Boolean checkLoginState(HttpServletRequest request, HttpServletResponse response);

    PageVO<UserVO> page(PageDTO pageDTO);

    Boolean updateRole(UpdateRoleDTO dto);

    Boolean deactivate(String userId);

    Boolean activate(String userId);

    Boolean uploadAvatar(UploadAvatarDTO dto);

    Boolean deleteAvatar();

    Boolean resetPwd(ResetPwdDTO dto);
}
