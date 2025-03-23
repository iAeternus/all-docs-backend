package org.ricky.core.user;

import org.junit.jupiter.api.Test;
import org.ricky.ApiTest;
import org.ricky.BaseApiTest;
import org.ricky.core.common.domain.page.PageDTO;
import org.ricky.core.common.domain.page.PageVO;
import org.ricky.core.user.domain.User;
import org.ricky.core.user.domain.dto.*;
import org.ricky.core.user.domain.vo.UserLoginVO;
import org.ricky.core.user.domain.vo.UserVO;
import org.ricky.util.SetUpResponse;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static java.nio.file.Files.readAllBytes;
import static org.junit.jupiter.api.Assertions.*;
import static org.ricky.common.constants.ConfigConstant.AVATAR_TYPES;
import static org.ricky.common.constants.ConfigConstant.USER_ID_PREFIX;
import static org.ricky.core.common.auth.PermissionEnum.ADMIN;
import static org.ricky.core.common.auth.PermissionEnum.USER;
import static org.ricky.core.user.domain.GenderEnum.MALE;
import static org.ricky.core.user.domain.User.newUserId;
import static org.ricky.util.RandomTestFixture.rPassword;
import static org.ricky.util.RandomTestFixture.rUsername;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/5
 * @className UserControllerTest
 * @desc
 */
class UserControllerTest extends BaseApiTest {

    static final String ROOT_URL = "/user";

    @Test
    void should_registry() {
        String userId = ApiTest.using(mockMvc)
                .post(ROOT_URL + "/registry")
                .body(RegistryUserDTO.builder().username(rUsername()).password(rPassword()).build())
                .execute()
                .expectStatus(200)
                .as(String.class);

        assertTrue(userId.startsWith(USER_ID_PREFIX));
    }

    @Test
    void should_fail_to_registry_if_username_already_exists() {
        // 注册以抢占用户名
        String username = rUsername();
        String password = rPassword();
        setUpApi.registry(username, password);

        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/registry")
                .body(RegistryUserDTO.builder().username(username).password(password).build())
                .execute()
                .expectStatus(409)
                .expectUserMessage("注册失败，用户名已存在");
    }

    @Test
    void should_login() {
        String username = rUsername();
        String password = rPassword();
        String userId = setUpApi.registry(username, password);

        UserLoginVO userLoginVO = ApiTest.using(mockMvc)
                .post(ROOT_URL + "/login")
                .body(UserLoginDTO.builder().username(username).password(password).build())
                .execute()
                .expectStatus(200)
                .as(UserLoginVO.class);

        assertEquals(userId, userLoginVO.getUserId());
        assertNotNull(userLoginVO.getToken());
    }

    @Test
    void should_fail_to_login_if_password_incorrect() {
        String username = rUsername();
        setUpApi.registry(username);

        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/login")
                .body(UserLoginDTO.builder().username(username).password("1234567").build())
                .execute()
                .expectStatus(401)
                .expectUserMessage("登录失败。");
    }

    @Test
    void should_get_user_by_id() {
        SetUpResponse operator = setUpApi.registryWithLogin();

        UserVO userVO = ApiTest.using(mockMvc)
                .get(ROOT_URL + "/id/{userId}", operator.getUserId())
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .as(UserVO.class);

        assertNotNull(userVO);
    }

    @Test
    void should_fail_to_get_if_user_id_not_exists() {
        SetUpResponse operator = setUpApi.registryWithLogin();

        ApiTest.using(mockMvc)
                .get(ROOT_URL + "/id/{userId}", newUserId())
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(404)
                .expectUserMessage("未找到资源。");
    }

    @Test
    void should_get_user_by_username() {
        String username = rUsername();
        SetUpResponse operator = setUpApi.registryWithLogin(username);

        UserVO userVO = ApiTest.using(mockMvc)
                .get(ROOT_URL + "/username/{userId}", username)
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .as(UserVO.class);

        assertEquals(userVO.getUsername(), username);
    }

    @Test
    void should_update() {
        SetUpResponse operator = setUpApi.registryWithLogin();
        UserDTO userDTO = UserDTO.builder()
                .id(operator.getUserId())
                .password("1234567")
                .gender(MALE)
                .birthday(LocalDate.of(2004, 7, 23))
                .build();

        ApiTest.using(mockMvc)
                .put(ROOT_URL)
                .body(userDTO)
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .expectSuccess();
    }

    @Test
    void should_delete_user_by_id() {
        UserLoginVO operator = setUpApi.adminLogin();
        String userId = setUpApi.registry();

        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/{userId}", userId)
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .expectSuccess();

        assertFalse(userRepository.exists(userId));
    }

    @Test
    void should_fail_to_delete_if_permission_not_admin() {
        SetUpResponse operator = setUpApi.registryWithLogin();
        String userId = setUpApi.registry();

        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/{userId}", userId)
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(401);
    }

    @Test
    void should_fail_to_delete_if_delete_self() {
        UserLoginVO operator = setUpApi.adminLogin();

        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/{userId}", operator.getUserId())
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(409)
                .expectUserMessage("不能删除自身");
    }

    @Test
    void should_delete_by_id_batch() {
        UserLoginVO operator = setUpApi.adminLogin();
        List<String> userIds = List.of(setUpApi.registry(), setUpApi.registry(), setUpApi.registry());

        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/batch")
                .bearerToken(operator.getToken())
                .body(DeleteByIdBatchDTO.builder().ids(userIds).build())
                .execute()
                .expectStatus(200)
                .expectSuccess();
    }

    @Test
    void should_fail_to_delete_batch_if_contains_self() {
        UserLoginVO operator = setUpApi.adminLogin();
        List<String> userIds = List.of(setUpApi.registry(), setUpApi.registry(), operator.getUserId());

        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/batch")
                .bearerToken(operator.getToken())
                .body(DeleteByIdBatchDTO.builder().ids(userIds).build())
                .execute()
                .expectStatus(409)
                .expectUserMessage("不能删除自身");
    }

    @Test
    void should_get_true_if_you_are_logged_in() {
        SetUpResponse operator = setUpApi.registryWithLogin();

        ApiTest.using(mockMvc)
                .get(ROOT_URL + "/login/state")
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .expectSuccess();
    }

    @Test
    void should_get_false_if_you_are_not_logged_in() {
        ApiTest.using(mockMvc)
                .get(ROOT_URL + "/login/state")
                .execute()
                .expectStatus(200)
                .expectFail();
    }

    @Test
    void should_page() {
        int cnt = 10;
        for (int i = 0; i < cnt; ++i) {
            setUpApi.registry();
        }
        UserLoginVO operator = setUpApi.adminLogin();

        PageVO<?> pageVO = ApiTest.using(mockMvc)
                .bearerToken(operator.getToken())
                .get(ROOT_URL + "/page")
                .body(PageDTO.builder()
                        .pageIndex(1)
                        .pageSize(3)
                        .build())
                .execute()
                .expectStatus(200)
                .as(PageVO.class);

        assertEquals(1, pageVO.getPageIndex());
        assertEquals(3, pageVO.getPageSize());
        assertEquals(3, pageVO.getData().size());
    }

    @Test
    void should_update_role() {
        UserLoginVO operator = setUpApi.adminLogin();
        String userId = setUpApi.registry();

        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/role")
                .bearerToken(operator.getToken())
                .body(UpdateRoleDTO.builder().userId(userId).newRole(ADMIN).build())
                .execute()
                .expectStatus(200)
                .expectSuccess();

        User user = userRepository.cachedById(userId);
        assertEquals(ADMIN, user.getPermission());
    }

    @Test
    void should_fail_to_update_if_update_self_role() {
        UserLoginVO operator = setUpApi.adminLogin();

        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/role")
                .bearerToken(operator.getToken())
                .body(UpdateRoleDTO.builder().userId(operator.getUserId()).newRole(USER).build())
                .execute()
                .expectStatus(409)
                .expectUserMessage("不能变更自身权限");
    }

    @Test
    void should_fail_to_update_if_given_same_role() {
        UserLoginVO operator = setUpApi.adminLogin();
        String userId = setUpApi.registry();

        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/role")
                .bearerToken(operator.getToken())
                .body(UpdateRoleDTO.builder().userId(userId).newRole(USER).build())
                .execute()
                .expectStatus(200)
                .expectFail();

        User user = userRepository.cachedById(userId);
        assertEquals(USER, user.getPermission());
    }

    /*
    作为最后一个执行的单测，产生了redis数据与mongo数据不一致的问题，(Ok)
    TODO 删除缓存的方法很离奇地被调用了多次 (Err)
    2025-03-12 19:52:16.861 [main] [INFO ] o.r.c.u.i.MongoCachedUserRepository:evictUserCache [] [all-docs-backend] Evicted cache for user[USR687613677062691840].
    2025-03-12 19:52:16.883 [main] [INFO ] o.r.c.u.i.MongoCachedUserRepository:evictUserCache [] [all-docs-backend] Evicted cache for user[USR687613677062691840].
    */
    @Test
    void should_deactivate() {
        UserLoginVO operator = setUpApi.adminLogin();
        String userId = setUpApi.registry();

        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/deactivate/{userId}", userId)
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .expectSuccess();

        User user = userRepository.cachedById(userId);
        assertTrue(user.isDeactivate());
    }

    @Test
    void should_fail_to_deactivate_if_deactivate_self() {
        UserLoginVO operator = setUpApi.adminLogin();

        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/deactivate/{userId}", operator.getUserId())
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(409)
                .expectUserMessage("不能封禁自己");
    }

    @Test
    void should_fail_to_deactivate_if_user_is_deactivated() {
        UserLoginVO operator = setUpApi.adminLogin();
        String userId = setUpApi.registry();

        // 先封禁，导致再封禁出现问题
        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/deactivate/{userId}", userId)
                .bearerToken(operator.getToken())
                .execute();

        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/deactivate/{userId}", userId)
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .expectFail();

        User user = userRepository.cachedById(userId);
        assertTrue(user.isDeactivate());
    }

    @Test
    void should_activate() {
        UserLoginVO operator = setUpApi.adminLogin();
        String userId = setUpApi.registry();

        // 先封禁
        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/deactivate/{userId}", userId)
                .bearerToken(operator.getToken())
                .execute();

        // 解封
        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/activate/{userId}", userId)
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .expectSuccess();

        User user = userRepository.cachedById(userId);
        assertTrue(user.isActivate());
    }

    @Test
    void should_fail_to_activate_if_activate_self() {
        UserLoginVO operator = setUpApi.adminLogin();

        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/activate/{userId}", operator.getUserId())
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(409)
                .expectUserMessage("不能解封自己");
    }

    @Test
    void should_fail_to_activate_if_user_is_activated() {
        UserLoginVO operator = setUpApi.adminLogin();
        String userId = setUpApi.registry();

        // 创建时就是未封禁状态，再解封就会出问题
        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/activate/{userId}", userId)
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .expectFail();

        User user = userRepository.cachedById(userId);
        assertTrue(user.isActivate());
    }

    @Test
    void should_upload_avatar() throws IOException {
        SetUpResponse operator = setUpApi.registryWithLogin();
        byte[] content = readAllBytes(Path.of("src/test/resources/avatar.jpg"));
        MockMultipartFile img = new MockMultipartFile("img", "avatar.jpg", AVATAR_TYPES[1], content);

        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/avatar")
                .bearerToken(operator.getToken())
                .param("userId", operator.getUserId())
                .file(img)
                .execute()
                .expectStatus(200)
                .expectSuccess();

        User user = userRepository.cachedById(operator.getUserId());
        assertNotNull(user.getAvatar());
        assertNotNull(user.getAvatarList());
    }

    @Test
    void should_fail_to_upload_avatar_if_content_type_not_support() throws IOException {
        SetUpResponse operator = setUpApi.registryWithLogin();
        byte[] content = readAllBytes(Path.of("src/test/resources/TXT测试.txt"));
        MockMultipartFile img = new MockMultipartFile("img", "TXT测试.txt", "text/plain", content);

        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/avatar")
                .bearerToken(operator.getToken())
                .param("userId", operator.getUserId())
                .file(img)
                .execute()
                .expectStatus(400)
                .expectUserMessage("不支持的文件类型");
    }

    @Test
    void should_delete_avatar() throws IOException {
        SetUpResponse operator = setUpApi.registryWithLogin();
        byte[] content = readAllBytes(Path.of("src/test/resources/avatar.jpg"));
        MockMultipartFile img = new MockMultipartFile("img", "avatar.jpg", AVATAR_TYPES[1], content);

        // 先上传一个，备删
        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/avatar")
                .bearerToken(operator.getToken())
                .param("userId", operator.getUserId())
                .file(img)
                .execute();

        // 删除
        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/avatar")
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .expectSuccess();
    }

    @Test
    void should_reset_pwd() {
        UserLoginVO operator = setUpApi.adminLogin();
        String userId = setUpApi.registry(rUsername(), "1234567");

        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/reset/pwd")
                .bearerToken(operator.getToken())
                .body(ResetPwdDTO.builder().userId(userId).build())
                .execute()
                .expectStatus(200)
                .expectSuccess();

        User user = userRepository.cachedById(userId);
        assertTrue(passwordEncoder.matches("123456", user.getPassword()));
    }

    @Test
    void should_fail_to_reset_if_permission_not_admin() {
        SetUpResponse operator = setUpApi.registryWithLogin();
        String userId = setUpApi.registry(rUsername(), "1234567");

        ApiTest.using(mockMvc)
                .put(ROOT_URL + "/reset/pwd")
                .bearerToken(operator.getToken())
                .body(ResetPwdDTO.builder().userId(userId).build())
                .execute()
                .expectStatus(401);
    }

}