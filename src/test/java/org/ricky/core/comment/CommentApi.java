package org.ricky.core.comment;

import org.ricky.ApiTest;
import org.ricky.BaseApiTest;
import org.ricky.core.comment.domain.dto.CreateCommentDTO;
import org.springframework.test.web.servlet.MockMvc;

import static org.ricky.util.RandomTestFixture.rSentence;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentApi
 * @desc
 */
public class CommentApi {

    private static final String ROOT_URL = "/comment";

    public static ApiTest.ResponseExecutor createRaw(MockMvc mockMvc, String token, CreateCommentDTO dto) {
        return ApiTest.using(mockMvc)
                .post(ROOT_URL)
                .bearerToken(token)
                .body(dto)
                .execute();
    }

    public static String create(MockMvc mockMvc, String token, CreateCommentDTO dto) {
        return createRaw(mockMvc, token, dto)
                .expectStatus(200)
                .as(String.class);
    }

    public static String create(MockMvc mockMvc, String token, String docId) {
        return create(mockMvc, token, CreateCommentDTO.builder()
                .docId(docId)
                .content(rSentence(10))
                .build());
    }

    public static String createWithParent(MockMvc mockMvc, String token, String docId, String parentCommentId) {
        return create(mockMvc, token, CreateCommentDTO.builder()
                .docId(docId)
                .parentCommentId(parentCommentId)
                .content(rSentence(10))
                .build());
    }

}
