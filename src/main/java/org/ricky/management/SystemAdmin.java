package org.ricky.management;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.context.ThreadLocalContext;
import org.ricky.common.context.UserContext;
import org.ricky.common.password.IPasswordEncoder;
import org.ricky.core.user.domain.User;
import org.ricky.core.user.domain.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/5
 * @className SystemAdmin
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemAdmin {

    public static final String ADMIN_UID = "USR000000000000000001";
    public static final String ADMIN_USERNAME = "ADMIN";
    public static final String ADMIN_PASSWORD = "123456";

    private final UserRepository userRepository;
    private final IPasswordEncoder passwordEncoder;

    @Transactional
    public void init() {
        if (userRepository.exists(ADMIN_UID)) {
            return;
        }

        ThreadLocalContext.setContext(UserContext.of(ADMIN_UID, ADMIN_USERNAME));
        String password = passwordEncoder.encode(ADMIN_PASSWORD);
        User systemAdmin = new User(ADMIN_UID, ADMIN_USERNAME, password);
        userRepository.save(systemAdmin);
        ThreadLocalContext.removeContext();
        log.info("Created All-Docs system admin.");
    }
}
