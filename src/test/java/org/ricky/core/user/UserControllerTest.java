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
    void should_registry() throws Exception {
        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/registry")
                .body(RegistryUserDTO.builder()
                        .username("Ricky")
                        .password("123456")
                        .build())
                .execute()
                .expectCode(200)
                .expectData("SUCCESS");
    }

}