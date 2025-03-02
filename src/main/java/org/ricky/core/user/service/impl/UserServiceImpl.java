package org.ricky.core.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.properties.SystemProperties;
import org.ricky.common.result.ApiResult;
import org.ricky.core.user.domain.dto.RegistryUserDTO;
import org.ricky.core.user.service.UserService;
import org.springframework.stereotype.Service;

import static org.ricky.common.constants.MessageConstants.OPERATE_FAILED;
import static org.ricky.common.constants.MessageConstants.PROCESS_ERROR_CODE;
import static org.ricky.common.result.ApiResult.error;
import static org.ricky.common.util.ValidationUtil.isFalse;

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

    @Override
    public ApiResult<String> registry(RegistryUserDTO userDTO) {
        if (isFalse(systemProperties.getUserRegistry())) {
            return error(PROCESS_ERROR_CODE, OPERATE_FAILED);
        }

        return null;
    }
}
