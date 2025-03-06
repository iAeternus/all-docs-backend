package org.ricky.core.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ricky.ApiTest;
import org.ricky.DocumentSharingSiteApplication;
import org.ricky.core.user.domain.dto.RegistryUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static java.nio.charset.Charset.defaultCharset;

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

    static final String ROOT_URL = "/user";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Rollback
    @Transactional
    void should_registry() {
        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/registry")
                .body(RegistryUserDTO.builder().username("Ricky").password("123456").build())
                .execute()
                .expectStatus(200)
                .expectData("SUCCESS");
    }

    @Test
    @Rollback
    @Transactional
    void should_fail_to_registry_if_username_already_exists() {
        // 注册以抢占用户名
        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/registry")
                .body(RegistryUserDTO.builder().username("Ricky").password("123456").build())
                .execute();

        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/registry")
                .body(RegistryUserDTO.builder().username("Ricky").password("123456").build())
                .charset(defaultCharset())
                .execute()
                .expectStatus(409)
                .expectUserMessage("注册失败，用户名已存在");
    }

}