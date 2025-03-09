package org.ricky.core.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.context.ThreadLocalContext;
import org.ricky.common.exception.MyException;
import org.ricky.common.properties.SystemProperties;
import org.ricky.common.ratelimit.RateLimiter;
import org.ricky.common.result.ApiResult;
import org.ricky.core.user.domain.User;
import org.ricky.core.user.domain.UserDomainService;
import org.ricky.core.user.domain.UserFactory;
import org.ricky.core.user.domain.UserRepository;
import org.ricky.core.user.domain.dto.RegistryUserDTO;
import org.ricky.core.user.domain.dto.UserLoginDTO;
import org.ricky.core.user.domain.vo.UserLoginVO;
import org.ricky.core.user.domain.vo.UserVO;
import org.ricky.core.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.ricky.common.constants.MessageConstants.SUCCESS;
import static org.ricky.common.exception.ErrorCodeEnum.AR_NOT_FOUND;
import static org.ricky.common.exception.ErrorCodeEnum.PROHIBITED_REGISTRATION;
import static org.ricky.common.ratelimit.TPSConstants.EXTREMELY_LOW_TPS;
import static org.ricky.common.ratelimit.TPSConstants.NORMAL_TPS;
import static org.ricky.common.util.ValidationUtil.isEmpty;
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

    private final RateLimiter rateLimiter;
    private final SystemProperties systemProperties;
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final UserFactory userFactory;

    @Override
    public ApiResult<String> registry(RegistryUserDTO userDTO) {
        rateLimiter.applyFor("User:Registry", EXTREMELY_LOW_TPS);

        if (isFalse(systemProperties.getUserRegistry())) {
            throw new MyException(PROHIBITED_REGISTRATION, "管理员禁止用户注册");
        }

        User user = userDomainService.registry(userDTO.getUsername(), userDTO.getPassword());
        userRepository.save(user);

        return ApiResult.success(user.getId());
    }

    @Override
    public ApiResult<String> registryBatch(List<RegistryUserDTO> userDTOS) {
        rateLimiter.applyFor("User:RegistryBatch", EXTREMELY_LOW_TPS);

        for (RegistryUserDTO userDTO : userDTOS) {
            registry(userDTO);
        }

        ThreadLocalContext.removeContext();
        return ApiResult.success(SUCCESS);
    }

    @Override
    public ApiResult<UserLoginVO> login(UserLoginDTO userDTO) {
        rateLimiter.applyFor("User:Login", NORMAL_TPS);

        UserLoginVO userVO = userDomainService.login(userDTO.getUsername(), userDTO.getPassword());
        userRepository.updateLastLogin(userVO.getUserId());
        return ApiResult.success(userVO);
    }

    @Override
    public ApiResult<UserVO> getById(String userId) {
        rateLimiter.applyFor("User:GetById", NORMAL_TPS);

        User user = userRepository.cachedById(userId);
        UserVO userVO = userFactory.user2vo(user);
        return ApiResult.success(userVO);
    }

    @Override
    public ApiResult<UserVO> getByUsername(String username) {
        rateLimiter.applyFor("User:GetByUsername", NORMAL_TPS);

        List<User> users = userRepository.listByUsername(username);
        if (isEmpty(users)) {
            throw new MyException(AR_NOT_FOUND, "user not found", Map.of("username", username));
        }
        UserVO userVO = userFactory.user2vo(users.get(0));
        return ApiResult.success(userVO);
    }

}
