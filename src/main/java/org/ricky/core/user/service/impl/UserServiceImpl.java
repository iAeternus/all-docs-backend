package org.ricky.core.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.auth.PermissionEnum;
import org.ricky.common.constants.MessageConstants;
import org.ricky.common.context.ThreadLocalContext;
import org.ricky.common.context.UserContext;
import org.ricky.common.properties.SystemProperties;
import org.ricky.common.result.ApiResult;
import org.ricky.core.user.domain.User;
import org.ricky.core.user.domain.UserRepository;
import org.ricky.core.user.domain.dto.RegistryUserDTO;
import org.ricky.core.user.service.UserService;
import org.ricky.management.SystemAdmin;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.ricky.common.auth.PermissionEnum.USER;
import static org.ricky.common.constants.MessageConstants.*;
import static org.ricky.common.result.ApiResult.error;
import static org.ricky.common.util.ValidationUtil.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className UserServiceImpl
 * @desc
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SystemProperties systemProperties;
    private final UserRepository userRepository;

    @Override
    public ApiResult<String> registry(RegistryUserDTO userDTO) {
        if (isFalse(systemProperties.getUserRegistry())) {
            return error(PROCESS_ERROR_CODE, OPERATE_FAILED);
        }

        ThreadLocalContext.setContext(UserContext.of(SystemAdmin.ADMIN_UID, SystemAdmin.ADMIN_USERNAME));
        List<User> users = userRepository.listByUsername(userDTO.getUsername());
        if(isNotEmpty(users)) {
            return ApiResult.error(PROCESS_ERROR_CODE, DATA_HAS_EXIST);
        }

        User user = new User(userDTO.getUsername(), userDTO.getPassword(), USER);
        userRepository.save(user);

        ThreadLocalContext.removeContext();
        return ApiResult.success(SUCCESS);
    }
}
