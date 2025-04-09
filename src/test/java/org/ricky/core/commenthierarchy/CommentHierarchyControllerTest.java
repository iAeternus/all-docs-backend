package org.ricky.core.commenthierarchy;

import org.junit.jupiter.api.Test;
import org.ricky.BaseApiTest;
import org.ricky.core.comment.CommentApi;
import org.ricky.core.commenthierarchy.domain.CommentHierarchy;
import org.ricky.core.commenthierarchy.domain.vo.CommentHierarchyVO;
import org.ricky.core.doc.DocApi;
import org.ricky.util.SetUpResponse;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ricky.core.doc.domain.FileTypeEnum.PDF;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentHierarchyControllerTest
 * @desc
 */
class CommentHierarchyControllerTest extends BaseApiTest {

    @Test
    void should_fetch_comment_hierarchy() throws IOException {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        String docId = DocApi.upload(mockMvc, operator.getToken(), "src/test/resources/PDF测试.pdf", PDF.getContentType());
        String commentId = CommentApi.create(mockMvc, operator.getToken(), docId);
        String commentId2 = CommentApi.create(mockMvc, operator.getToken(), docId);
        String commentId3 = CommentApi.createWithParent(mockMvc, operator.getToken(), docId, commentId);

        // When
        CommentHierarchyVO hierarchy = CommentHierarchyApi.fetchCommentHierarchy(mockMvc, operator.getToken(), docId);

        // Then
        List<String> commentIds = hierarchy.getAllComments().stream().map(CommentHierarchyVO.CommentVO::getId).collect(toImmutableList());
        assertEquals(3, commentIds.size());
        assertTrue(commentIds.containsAll(List.of(commentId, commentId2, commentId3)));

        CommentHierarchy commentHierarchy = commentHierarchyRepository.byDocId(docId);
        assertEquals(commentHierarchy.getIdTree(), hierarchy.getIdTree());

        // Finally
        tearDownApi.removeDoc(operator.getToken(), docId);
    }

}