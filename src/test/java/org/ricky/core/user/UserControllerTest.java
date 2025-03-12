package org.ricky.core.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ricky.ApiTest;
import org.ricky.DocumentSharingSiteApplication;
import org.ricky.common.auth.PermissionEnum;
import org.ricky.common.domain.PageDTO;
import org.ricky.common.domain.PageVO;
import org.ricky.core.user.domain.dto.*;
import org.ricky.core.user.domain.vo.UserLoginVO;
import org.ricky.core.user.domain.vo.UserVO;
import org.ricky.util.SetUpApi;
import org.ricky.util.SetUpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.*;
import static org.ricky.common.auth.PermissionEnum.ADMIN;
import static org.ricky.common.auth.PermissionEnum.USER;
import static org.ricky.common.constants.ConfigConstant.USER_ID_PREFIX;
import static org.ricky.core.user.domain.GenderEnum.MALE;
import static org.ricky.core.user.domain.User.newUserId;
import static org.ricky.util.SetUpApi.TEST_PASSWORD;
import static org.ricky.util.SetUpApi.TEST_USERNAME;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/5
 * @className UserControllerTest
 * @desc
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DocumentSharingSiteApplication.class)
class UserControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    SetUpApi setUpApi;

    static final String ROOT_URL = "/user";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Rollback
    @Transactional
    void should_registry() {
        String userId = ApiTest.using(mockMvc)
                .post(ROOT_URL + "/registry")
                .body(RegistryUserDTO.builder().username(TEST_USERNAME).password(TEST_PASSWORD).build())
                .execute()
                .expectStatus(200)
                .as(String.class);
        assertTrue(userId.startsWith(USER_ID_PREFIX));
    }

    @Test
    @Rollback
    @Transactional
    void should_fail_to_registry_if_username_already_exists() {
        // 注册以抢占用户名
        setUpApi.registry(TEST_USERNAME, TEST_PASSWORD);
        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/registry")
                .body(RegistryUserDTO.builder().username(TEST_USERNAME).password(TEST_PASSWORD).build())
                .charset(defaultCharset())
                .execute()
                .expectStatus(409)
                .expectUserMessage("注册失败，用户名已存在");
    }

    @Test
    @Rollback
    @Transactional
    void should_login() {
        String userId = setUpApi.registry(TEST_USERNAME, TEST_PASSWORD);
        UserLoginVO userLoginVO = ApiTest.using(mockMvc)
                .post(ROOT_URL + "/login")
                .body(UserLoginDTO.builder().username(TEST_USERNAME).password(TEST_PASSWORD).build())
                .execute()
                .expectStatus(200)
                .as(UserLoginVO.class);

        assertEquals(userId, userLoginVO.getUserId());
        assertNotNull(userLoginVO.getToken());
    }

    @Test
    @Rollback
    @Transactional
    void should_fail_to_login_if_password_incorrect() {
        setUpApi.registry(TEST_USERNAME, TEST_PASSWORD);
        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/login")
                .body(UserLoginDTO.builder().username(TEST_USERNAME).password("1234567").build())
                .execute()
                .expectStatus(401)
                .expectUserMessage("登录失败。");
    }

    @Test
    @Rollback
    @Transactional
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
    @Rollback
    @Transactional
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
    @Rollback
    @Transactional
    void should_get_user_by_username() {
        SetUpResponse operator = setUpApi.registryWithLogin(TEST_USERNAME, TEST_PASSWORD);
        UserVO userVO = ApiTest.using(mockMvc)
                .get(ROOT_URL + "/username/{userId}", TEST_USERNAME)
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .as(UserVO.class);

        assertEquals(userVO.getUsername(), TEST_USERNAME);
    }

    @Test
    @Rollback
    @Transactional
    void should_update() {
        SetUpResponse operator = setUpApi.registryWithLogin(TEST_USERNAME, TEST_PASSWORD);

        ApiTest.using(mockMvc)
                .put(ROOT_URL)
                .body(UserDTO.builder()
                        .id(operator.getUserId())
                        .password("1234567")
                        .gender(MALE)
                        .birthday(LocalDate.of(2004, 7, 23))
                        .build())
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .expectSuccess();
    }

    @Test
    @Rollback
    @Transactional
    void should_delete_user_by_id() {
        UserLoginVO operator = setUpApi.adminLogin();
        String userId = setUpApi.registry(TEST_USERNAME, TEST_PASSWORD);

        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/{userId}", userId)
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(200)
                .expectSuccess();
    }

    @Test
    @Rollback
    @Transactional
    void should_fail_to_delete_if_permission_not_admin() {
        SetUpResponse operator = setUpApi.registryWithLogin();
        String userId = setUpApi.registry(TEST_USERNAME, TEST_PASSWORD);
        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/{userId}", userId)
                .bearerToken(operator.getToken())
                .execute()
                .expectStatus(401);
    }

    @Test
    @Rollback
    @Transactional
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
    @Rollback
    @Transactional
    void should_delete_by_id_batch() {
        UserLoginVO operator = setUpApi.adminLogin();
        List<String> userIds = List.of(
                setUpApi.registry(TEST_USERNAME + "1"),
                setUpApi.registry(TEST_USERNAME + "2"),
                setUpApi.registry(TEST_USERNAME + "3")
        );

        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/batch")
                .bearerToken(operator.getToken())
                .body(DeleteByIdBatchDTO.builder().ids(userIds).build())
                .execute()
                .expectStatus(200)
                .expectSuccess();
    }

    @Test
    @Rollback
    @Transactional
    void should_fail_to_delete_batch_if_contains_self() {
        UserLoginVO operator = setUpApi.adminLogin();
        List<String> userIds = List.of(
                setUpApi.registry(TEST_USERNAME + "1"),
                setUpApi.registry(TEST_USERNAME + "2"),
                operator.getUserId()
        );
        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/batch")
                .bearerToken(operator.getToken())
                .body(DeleteByIdBatchDTO.builder().ids(userIds).build())
                .execute()
                .expectStatus(409)
                .expectUserMessage("不能删除自身");
    }

    @Test
    @Rollback
    @Transactional
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
    @Rollback
    @Transactional
    void should_get_false_if_you_are_not_logged_in() {
        ApiTest.using(mockMvc)
                .get(ROOT_URL + "/login/state")
                .execute()
                .expectStatus(200)
                .expectSuccess();
    }

    @Test
    @Rollback
    @Transactional
    void should_page() {
        int cnt = 10;
        for (int i = 0; i < cnt; ++i) {
            setUpApi.registry(TEST_USERNAME + i);
        }
        UserLoginVO operator = setUpApi.adminLogin();

        PageVO<?> pageVO = ApiTest.using(mockMvc)
                .bearerToken(operator.getToken())
                .get(ROOT_URL + "/page")
                .body(PageDTO.builder()
                        .pageNum(1)
                        .pageSize(3)
                        .build())
                .execute()
                .expectStatus(200)
                .as(PageVO.class);

        assertEquals(1, pageVO.getPageNum());
        assertEquals(3, pageVO.getPageSize());
        assertEquals(3, pageVO.getData().size());
    }

    @Test
    @Rollback
    @Transactional
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
    }

    @Test
    @Rollback
    @Transactional
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
    @Rollback
    @Transactional
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
    }

}