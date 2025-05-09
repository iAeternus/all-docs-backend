package org.ricky.core.user.service.impl;

import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.exception.MyException;
import org.ricky.common.password.IPasswordEncoder;
import org.ricky.common.properties.SystemProperties;
import org.ricky.common.ratelimit.RateLimiter;
import org.ricky.core.common.context.ThreadLocalContext;
import org.ricky.core.common.domain.page.PageDTO;
import org.ricky.core.common.domain.page.PageVO;
import org.ricky.core.common.util.JwtUtil;
import org.ricky.core.common.util.ValidationUtil;
import org.ricky.core.user.domain.User;
import org.ricky.core.user.domain.UserDomainService;
import org.ricky.core.user.domain.UserFactory;
import org.ricky.core.user.domain.UserRepository;
import org.ricky.core.user.domain.dto.*;
import org.ricky.core.user.domain.vo.UserLoginVO;
import org.ricky.core.user.domain.vo.UserVO;
import org.ricky.core.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.ricky.common.constants.ConfigConstant.AUTHORIZATION;
import static org.ricky.common.constants.ConfigConstant.BEARER;
import static org.ricky.common.exception.ErrorCodeEnum.*;
import static org.ricky.common.ratelimit.TPSConstants.*;
import static org.ricky.core.common.util.ValidationUtil.*;

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
    private final IPasswordEncoder passwordEncoder;

    @Override
    public String registry(RegistryUserDTO userDTO) {
        rateLimiter.applyFor("User:Registry", EXTREMELY_LOW_TPS);

        if (isFalse(systemProperties.getUserRegistry())) {
            throw new MyException(PROHIBITED_REGISTRATION, "管理员禁止用户注册");
        }

        User user = userDomainService.registry(userDTO.getUsername(), userDTO.getPassword());
        userRepository.save(user);

        return user.getId();
    }

    @Override
    public Boolean registryBatch(List<RegistryUserDTO> userDTOS) {
        rateLimiter.applyFor("User:RegistryBatch", EXTREMELY_LOW_TPS);

        for (RegistryUserDTO userDTO : userDTOS) {
            registry(userDTO);
        }

        ThreadLocalContext.removeContext();
        return true;
    }

    @Override
    @Transactional
    public UserLoginVO login(UserLoginDTO userDTO) {
        rateLimiter.applyFor("User:Login", NORMAL_TPS);

        UserLoginVO userVO = userDomainService.login(userDTO.getUsername(), userDTO.getPassword());
        userRepository.updateLastLogin(userVO.getUserId());
        return userVO;
    }

    @Override
    public UserVO getById(String userId) {
        rateLimiter.applyFor("User:GetById", NORMAL_TPS);

        User user = userRepository.cachedById(userId);
        return userFactory.user2vo(user);
    }

    @Override
    public UserVO getByUsername(String username) {
        rateLimiter.applyFor("User:GetByUsername", NORMAL_TPS);

        List<User> users = userRepository.listByUsername(username);
        if (isEmpty(users)) {
            throw new MyException(AR_NOT_FOUND, "user not found", Map.of("username", username));
        }
        return userFactory.user2vo(users.get(0));
    }

    @Override
    public Boolean updateById(UserDTO userDTO) {
        rateLimiter.applyFor("User:Update", MINIMUM_TPS);

        User user = userRepository.cachedById(userDTO.getId());
        String encodePassword = passwordEncoder.encode(userDTO.getPassword());
        user.update(encodePassword, userDTO.getMobile(), userDTO.getEmail(), userDTO.getGender(), userDTO.getDescription(), userDTO.getBirthday());
        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional
    public Boolean deleteById(String userId) {
        rateLimiter.applyFor("User:DeleteById", MINIMUM_TPS);

        if (ThreadLocalContext.getContext().isSelf(userId)) {
            throw new MyException(CANNOT_DELETE_SELF, "不能删除自身", Map.of("userId", userId));
        }

        User user = userRepository.cachedById(userId);
        userRepository.deleteById(user);

        // 删除头像
        userRepository.deleteAvatars(user.getAvatarList().stream().collect(toImmutableSet()));

        return true;
    }

    @Override
    @Transactional
    public Boolean deleteByIdBatch(DeleteByIdBatchDTO dto) {
        rateLimiter.applyFor("User:DeleteByIdBatch", MINIMUM_TPS);

        Set<String> ids = dto.getIds().stream().collect(toImmutableSet());
        String selfUid = ThreadLocalContext.getContext().getUid();
        if (dto.getIds().stream().anyMatch(s -> ValidationUtil.equals(selfUid, s))) {
            throw new MyException(CANNOT_DELETE_SELF, "不能删除自身", Map.of("userIds", ids));
        }

        List<User> users = userRepository.listByIds(ids);
        userRepository.delete(users);

        // 删除头像
        Set<String> avatars = users.stream()
                .flatMap(user -> user.getAvatarList().stream())
                .collect(toImmutableSet());
        userRepository.deleteAvatars(avatars);

        return true;
    }

    @Override
    public Boolean checkLoginState(HttpServletRequest request, HttpServletResponse response) {
        rateLimiter.applyFor("User:CheckLoginState", EXTREMELY_HIGH_TPS);

        // 缓存2s，避免前端频繁刷新
        response.setHeader("Cache-Control", "max-age=2, public");

        // 获取token
        final String token = request.getHeader(AUTHORIZATION);
        if (isBlank(token)) {
            return false;
        }

        // 解析token
        Map<String, Claim> claim = JwtUtil.verifyToken(token.substring(BEARER.length()));
        return isNotEmpty(claim);
    }

    @Override
    public PageVO<UserVO> page(PageDTO pageDTO) {
        rateLimiter.applyFor("User:Page", NORMAL_TPS);

        List<User> users = userRepository.page(pageDTO.getPageIndex(), pageDTO.getPageSize());

        return PageVO.<UserVO>builder()
                .totalCnt(users.size())
                .pageIndex(pageDTO.getPageIndex())
                .pageSize(pageDTO.getPageSize())
                .data(users.stream().map(userFactory::user2vo).collect(toImmutableList()))
                .build();
    }

    @Override
    public Boolean updateRole(UpdateRoleDTO dto) {
        rateLimiter.applyFor("User:UpdateRole", MINIMUM_TPS);

        if (ThreadLocalContext.getContext().isSelf(dto.getUserId())) {
            throw new MyException(CANNOT_UPDATE_SELF_ROLE, "不能变更自身权限", Map.of("userId", dto.getUserId()));
        }

        User user = userRepository.cachedById(dto.getUserId());
        if (user.getPermission() == dto.getNewRole()) {
            return false;
        }
        user.updateRole(dto.getNewRole());
        userRepository.save(user);

        return true;
    }

    @Override
    public Boolean deactivate(String userId) {
        rateLimiter.applyFor("User:Deactivate", MINIMUM_TPS);

        if (ThreadLocalContext.getContext().isSelf(userId)) {
            throw new MyException(CANNOT_DEACTIVATE_SELF, "不能封禁自己", Map.of("userId", userId));
        }

        User user = userRepository.cachedById(userId);
        if (user.isDeactivate()) {
            return false;
        }
        user.deactivate();
        userRepository.save(user);

        return true;
    }

    @Override
    public Boolean activate(String userId) {
        rateLimiter.applyFor("User:Activate", MINIMUM_TPS);

        if (ThreadLocalContext.getContext().isSelf(userId)) {
            throw new MyException(CANNOT_ACTIVATE_SELF, "不能解封自己", Map.of("userId", userId));
        }

        User user = userRepository.cachedById(userId);
        if (user.isActivate()) {
            return false;
        }
        user.activate();
        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional
    public Boolean uploadAvatar(UploadAvatarDTO dto) {
        rateLimiter.applyFor("User:UploadAvatar", MINIMUM_TPS);

        User user = userRepository.cachedById(dto.getUserId());
        String gridFsId = userRepository.uploadAvatar(dto.getUserId(), dto.getImg());
        user.addAvatar(gridFsId);
        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional
    public Boolean deleteAvatar() {
        rateLimiter.applyFor("User:DeleteAvatar", MINIMUM_TPS);

        String userId = ThreadLocalContext.getContext().getUid();
        User user = userRepository.cachedById(userId);
        userRepository.deleteAvatar(userId, user.getAvatar());
        user.removeAvatar();
        userRepository.save(user);

        return true;
    }

    @Override
    public Boolean resetPwd(ResetPwdDTO dto) {
        rateLimiter.applyFor("User:ResetPwd", MINIMUM_TPS);

        User user = userRepository.cachedById(dto.getUserId());
        userDomainService.resetPwd(user);
        userRepository.save(user);

        return true;
    }

}
