package org.ricky.util;

import org.ricky.ApiTest;
import org.ricky.core.doc.domain.dto.RemoveDocDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/18
 * @className TearDownApi
 * @desc
 */
@Component
public class TearDownApi {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    public void removeDoc(String token, String docId) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ApiTest.using(mockMvc)
                .delete("/doc")
                .bearerToken(token)
                .body(RemoveDocDTO.builder()
                        .docId(docId)
                        .isDeleteFile(false)
                        .build())
                .execute();
    }

}
