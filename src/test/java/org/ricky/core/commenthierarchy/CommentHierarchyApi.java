package org.ricky.core.commenthierarchy;

import org.ricky.ApiTest;
import org.ricky.core.commenthierarchy.domain.vo.CommentHierarchyVO;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentHierarchyApi
 * @desc
 */
public class CommentHierarchyApi {

    private static final String ROOT_URL = "/comment_hierarchies";

    public static ApiTest.ResponseExecutor fetchCommentHierarchyRaw(MockMvc mockMvc, String token, String docId) {
        return ApiTest.using(mockMvc)
                .get(ROOT_URL + "/comments/{docId}", docId)
                .bearerToken(token)
                .execute();
    }

    public static CommentHierarchyVO fetchCommentHierarchy(MockMvc mockMvc, String token, String docId) {
        return fetchCommentHierarchyRaw(mockMvc, token, docId)
                .expectStatus(200)
                .as(CommentHierarchyVO.class);
    }

}
