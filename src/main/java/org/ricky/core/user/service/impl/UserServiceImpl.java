package org.ricky.core.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.context.ThreadLocalContext;
import org.ricky.common.exception.MyException;
import org.ricky.common.properties.SystemProperties;
import org.ricky.common.ratelimit.RateLimiter;
import org.ricky.common.ratelimit.TPSConstants;
import org.ricky.common.result.ApiResult;
import org.ricky.core.user.domain.User;
import org.ricky.core.user.domain.UserDomainService;
import org.ricky.core.user.domain.UserRepository;
import org.ricky.core.user.domain.dto.RegistryUserDTO;
import org.ricky.core.user.service.UserService;
import org.springframework.stereotype.Service;

import static org.ricky.common.constants.MessageConstants.SUCCESS;
import static org.ricky.common.exception.ErrorCodeEnum.PROHIBITED_REGISTRATION;
import static org.ricky.common.ratelimit.TPSConstants.EXTREMELY_LOW_TPS;
import static org.ricky.common.util.ValidationUtil.isFalse;
import static org.ricky.common.util.ValidationUtil.isNotEmpty;

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

    private final RateLimiter rateLimiter;
    private final SystemProperties systemProperties;
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;

    @Override
    public ApiResult<String> registry(RegistryUserDTO userDTO) {
        rateLimiter.applyFor("User:Registry", EXTREMELY_LOW_TPS);

        if (isFalse(systemProperties.getUserRegistry())) {
            throw new MyException(PROHIBITED_REGISTRATION, "管理员禁止用户注册");
        }

        User user = userDomainService.registry(userDTO.getUsername(), userDTO.getPassword());
        userRepository.save(user);

        ThreadLocalContext.removeContext();
        return ApiResult.success(SUCCESS);
    }

}
