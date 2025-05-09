package org.ricky.util;

import org.ricky.ApiTest;
import org.ricky.core.user.domain.dto.RegistryUserDTO;
import org.ricky.core.user.domain.dto.UserLoginDTO;
import org.ricky.core.user.domain.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.ricky.management.SystemAdmin.ADMIN_PASSWORD;
import static org.ricky.management.SystemAdmin.ADMIN_USERNAME;
import static org.ricky.util.RandomTestFixture.rPassword;
import static org.ricky.util.RandomTestFixture.rUsername;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/9
 * @className SetUpApi
 * @desc
 */
@Component
public class SetUpApi {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    public String registry() {
        return registry(rUsername(), rPassword());
    }

    public String registry(String username) {
        return registry(username, rPassword());
    }

    public String registry(String username, String password) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        return ApiTest.using(mockMvc)
                .post("/user/registry")
                .body(RegistryUserDTO.builder().username(username).password(password).build())
                .execute()
                .as(String.class);
    }

    public UserLoginVO adminLogin() {
        return login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    public UserLoginVO login(String username, String password) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        return ApiTest.using(mockMvc)
                .post("/user/login")
                .body(UserLoginDTO.builder().username(username).password(password).build())
                .execute()
                .expectStatus(200)
                .as(UserLoginVO.class);
    }

    public SetUpResponse registryWithLogin() {
        return registryWithLogin(rUsername(), rPassword());
    }

    public SetUpResponse registryWithLogin(String username) {
        return registryWithLogin(username, rPassword());
    }

    public SetUpResponse registryWithLogin(String username, String password) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        registry(username, password);
        UserLoginVO userLoginVO = login(username, password);

        return SetUpResponse.builder()
                .userId(userLoginVO.getUserId())
                .token(userLoginVO.getToken())
                .build();
    }

}
