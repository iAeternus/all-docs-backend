package org.ricky.core.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ricky.ApiTest;
import org.ricky.DocumentSharingSiteApplication;
import org.ricky.core.user.domain.dto.RegistryUserDTO;
import org.ricky.core.user.domain.dto.UserDTO;
import org.ricky.core.user.domain.dto.UserLoginDTO;
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

import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.*;
import static org.ricky.common.constants.ConfigConstant.USER_ID_PREFIX;
import static org.ricky.common.constants.MessageConstants.SUCCESS;
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
        SetUpResponse response = setUpApi.registryWithLogin();
        UserVO userVO = ApiTest.using(mockMvc)
                .get(ROOT_URL + "/id/{userId}", response.getUserId())
                .bearerToken(response.getToken())
                .execute()
                .expectStatus(200)
                .as(UserVO.class);

        assertNotNull(userVO);
    }

    @Test
    @Rollback
    @Transactional
    void should_fail_to_get_if_user_id_not_exists() {
        SetUpResponse response = setUpApi.registryWithLogin();
        ApiTest.using(mockMvc)
                .get(ROOT_URL + "/id/{userId}", newUserId())
                .bearerToken(response.getToken())
                .execute()
                .expectStatus(404)
                .expectUserMessage("未找到资源。");
    }

    @Test
    @Rollback
    @Transactional
    void should_get_user_by_username() {
        SetUpResponse response = setUpApi.registryWithLogin(TEST_USERNAME, TEST_PASSWORD);
        UserVO userVO = ApiTest.using(mockMvc)
                .get(ROOT_URL + "/username/{userId}", TEST_USERNAME)
                .bearerToken(response.getToken())
                .execute()
                .expectStatus(200)
                .as(UserVO.class);

        assertEquals(userVO.getUsername(), TEST_USERNAME);
    }

    @Test
    @Rollback
    @Transactional
    void should_update() {
        SetUpResponse response = setUpApi.registryWithLogin(TEST_USERNAME, TEST_PASSWORD);
        String res = ApiTest.using(mockMvc)
                .put(ROOT_URL)
                .body(UserDTO.builder()
                        .id(response.getUserId())
                        .password("1234567")
                        .gender(MALE)
                        .birthday(LocalDate.of(2004, 7, 23))
                        .build())
                .bearerToken(response.getToken())
                .execute()
                .expectStatus(200)
                .as(String.class);

        assertEquals(SUCCESS, res);
    }

    @Test
    @Rollback
    @Transactional
    void should_delete_user_by_id() {
        UserLoginVO userLoginVO = setUpApi.adminLogin();
        String userId = setUpApi.registry(TEST_USERNAME, TEST_PASSWORD);
        String res = ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/{userId}", userId)
                .bearerToken(userLoginVO.getToken())
                .execute()
                .expectStatus(200)
                .as(String.class);

        assertEquals(SUCCESS, res);
    }

    @Test
    @Rollback
    @Transactional
    void should_fail_to_delete_if_permission_not_admin() {
        SetUpResponse response = setUpApi.registryWithLogin();
        String userId = setUpApi.registry(TEST_USERNAME, TEST_PASSWORD);
        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/{userId}", userId)
                .bearerToken(response.getToken())
                .execute()
                .expectStatus(401);
    }

    @Test
    @Rollback
    @Transactional
    void should_fail_to_delete_if_delete_self() {
        UserLoginVO userLoginVO = setUpApi.adminLogin();
        ApiTest.using(mockMvc)
                .delete(ROOT_URL + "/{userId}", userLoginVO.getUserId())
                .bearerToken(userLoginVO.getToken())
                .execute()
                .expectStatus(409)
                .expectUserMessage("不能删除自身");
    }

}