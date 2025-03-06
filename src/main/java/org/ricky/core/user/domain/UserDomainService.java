package org.ricky.core.user.domain;

import lombok.RequiredArgsConstructor;
import org.ricky.common.context.ThreadLocalContext;
import org.ricky.common.context.UserContext;
import org.ricky.common.exception.MyException;
import org.ricky.common.password.IPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.ricky.common.auth.PermissionEnum.USER;
import static org.ricky.common.exception.ErrorCodeEnum.USER_NAME_ALREADY_EXISTS;
import static org.ricky.common.util.ValidationUtil.isNotEmpty;
import static org.ricky.management.SystemAdmin.ADMIN_UID;
import static org.ricky.management.SystemAdmin.ADMIN_USERNAME;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className UserDomainService
 * @desc
 */
@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;
    private final IPasswordEncoder passwordEncoder;

    public User registry(String username, String password) {
        ThreadLocalContext.setContext(UserContext.of(ADMIN_UID, ADMIN_USERNAME));
        List<User> users = userRepository.listByUsername(username);
        if (isNotEmpty(users)) {
            throw new MyException(USER_NAME_ALREADY_EXISTS, "注册失败，用户名已存在",
                    Map.of("username", username));
        }
        return new User(username, passwordEncoder.encode(password), USER);
    }

}
