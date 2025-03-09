package org.ricky.core.user.service;

import lombok.RequiredArgsConstructor;
import org.ricky.common.auth.PermissionEnum;
import org.ricky.common.context.ThreadLocalContext;
import org.ricky.common.context.UserContext;
import org.ricky.common.password.IPasswordEncoder;
import org.ricky.core.user.domain.User;
import org.ricky.core.user.domain.UserRepository;
import org.ricky.management.SystemAdmin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static org.ricky.common.auth.PermissionEnum.ADMIN;
import static org.ricky.management.SystemAdmin.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/9
 * @className InitSystemAdmin
 * @desc 初始化系统管理员
 */
@Component
@RequiredArgsConstructor
public class InitSystemAdmin implements CommandLineRunner {

    private final UserRepository userRepository;
    private final IPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.exists(ADMIN_UID)) {
            return;
        }

        ThreadLocalContext.setContext(UserContext.of(ADMIN_UID, ADMIN_USERNAME));
        String password = passwordEncoder.encode(ADMIN_PASSWORD);
        User systemAdmin = new User(ADMIN_UID, ADMIN_USERNAME, password);
        userRepository.save(systemAdmin);
        ThreadLocalContext.removeContext();
    }
}
