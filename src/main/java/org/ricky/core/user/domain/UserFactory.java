package org.ricky.core.user.domain;

import lombok.RequiredArgsConstructor;
import org.ricky.common.password.IPasswordEncoder;
import org.ricky.core.user.domain.vo.UserVO;
import org.springframework.stereotype.Component;

import static org.ricky.common.auth.PermissionEnum.USER;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/9
 * @className UserFactory
 * @desc
 */
@Component
@RequiredArgsConstructor
public class UserFactory {

    private final IPasswordEncoder passwordEncoder;

    public User registry(String username, String password) {
        return new User(username, passwordEncoder.encode(password), USER);
    }

    public UserVO user2vo(User user) {
        return UserVO.builder()
                .username(user.getUsername())
                .mobile(user.getMobile())
                .email(user.getEmail())
                .gender(user.getGender())
                .description(user.getDescription())
                .avatarList(user.getAvatarList())
                .avatar(user.getAvatar())
                .birthday(user.getBirthday())
                .banning(user.getBanning())
                .permission(user.getPermission())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
