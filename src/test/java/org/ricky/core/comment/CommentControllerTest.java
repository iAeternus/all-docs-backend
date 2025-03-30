package org.ricky.core.comment;

import org.junit.jupiter.api.Test;
import org.ricky.BaseApiTest;
import org.ricky.core.comment.domain.Comment;
import org.ricky.core.comment.domain.dto.CreateCommentDTO;
import org.ricky.core.comment.domain.event.CommentCreatedEvent;
import org.ricky.core.commenthierarchy.domain.CommentHierarchy;
import org.ricky.core.doc.DocApi;
import org.ricky.util.SetUpResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.ricky.core.common.domain.event.DomainEventTypeEnum.COMMENT_CREATED;
import static org.ricky.core.doc.domain.FileTypeEnum.PDF;
import static org.ricky.util.RandomTestFixture.rSentence;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentControllerTest
 * @desc
 */
class CommentControllerTest extends BaseApiTest {

    @Test
    void should_create_comment() throws IOException {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        String docId = DocApi.upload(mockMvc, operator.getToken(), "src/test/resources/PDF测试.pdf", PDF.getContentType());

        String content = rSentence(10);
        CreateCommentDTO dto = CreateCommentDTO.builder()
                .docId(docId)
                .content(content)
                .build();

        // When
        String commentId = CommentApi.create(mockMvc, operator.getToken(), dto);

        // Then
        Comment comment = commentRepository.byId(commentId);
        assertEquals(content, comment.getContent());

        CommentCreatedEvent evt = domainEventDao.latestEventFor(commentId, COMMENT_CREATED, CommentCreatedEvent.class);
        assertEquals(commentId, evt.getCommentId());

        CommentHierarchy commentHierarchy = commentHierarchyRepository.byDocId(docId);
        assertEquals(1, commentHierarchy.getAllCommentIds().size());
        assertTrue(commentHierarchy.containsCommentId(commentId));

        // Finally
        tearDownApi.removeDoc(operator.getToken(), docId);
    }

    @Test
    void should_create_comment_with_parent() throws IOException {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        String docId = DocApi.upload(mockMvc, operator.getToken(), "src/test/resources/PDF测试.pdf", PDF.getContentType());
        String parentCommentId = CommentApi.create(mockMvc, operator.getToken(), CreateCommentDTO.builder().docId(docId).content(rSentence(10)).build());

        CreateCommentDTO dto = CreateCommentDTO.builder()
                .docId(docId)
                .parentCommentId(parentCommentId)
                .content(rSentence(10))
                .build();

        // When
        String subCommentId = CommentApi.create(mockMvc, operator.getToken(), dto);

        // Then
        Comment subComment = commentRepository.byId(subCommentId);
        assertNotNull(subComment);

        CommentHierarchy commentHierarchy = commentHierarchyRepository.byDocId(docId);
        assertTrue(commentHierarchy.containsCommentId(parentCommentId));
        assertTrue(commentHierarchy.containsCommentId(subCommentId));

        // Finally
        tearDownApi.removeDoc(operator.getToken(), docId);
    }

    @Test
    void create_comment_should_also_sync_doc() {
        // TODO
    }

}