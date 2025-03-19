package org.ricky.core.doc;

import org.junit.jupiter.api.Test;
import org.ricky.BaseApiTest;
import org.ricky.core.category.CategoryApi;
import org.ricky.core.category.domain.dto.CategoryDTO;
import org.ricky.core.category.domain.dto.ConnectDTO;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.dto.RemoveDocDTO;
import org.ricky.core.doc.domain.dto.UploadDocDTO;
import org.ricky.core.doc.domain.event.DocCreatedEvent;
import org.ricky.util.SetUpResponse;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.readAllBytes;
import static org.junit.jupiter.api.Assertions.*;
import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_CREATED;
import static org.ricky.core.doc.domain.FileTypeEnum.DOC;
import static org.ricky.core.doc.domain.FileTypeEnum.PDF;
import static org.ricky.util.RandomTestFixture.rSentence;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className DocControllerTest
 * @desc
 */
class DocControllerTest extends BaseApiTest {

    @Test
    void should_upload_doc_with_review() throws IOException {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        byte[] content = readAllBytes(Path.of("src/test/resources/PDF测试.pdf"));
        MockMultipartFile file = new MockMultipartFile("file", "PDF测试.pdf", DOC.getContentType(), content);
        UploadDocDTO dto = UploadDocDTO.builder().file(file).build();

        // When
        String docId = DocApi.upload(mockMvc, operator.getToken(), dto);

        // Then
        assertNotNull(docId);
        Doc doc = docRepository.cachedById(docId);
        assertEquals("PDF测试.pdf", doc.getName());
        assertNotNull(doc.getGridFsId());
        assertNotNull(doc.getTxtId());
        assertNotNull(doc.getThumbId());

        DocCreatedEvent evt = domainEventDao.latestEventFor(docId, DOC_CREATED, DocCreatedEvent.class);
        assertEquals(docId, evt.getDocId());
        assertEquals(1, evt.getConsumedCount());
        assertEquals("PDF", tagRepository.byId(doc.getTagIds().get(0)).getName());

        // Finally
        tearDownApi.removeDoc(operator.getToken(), docId);
    }

    @Test
    void should_fail_to_upload_if_doc_is_already_exists() throws IOException {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        byte[] content = readAllBytes(Path.of("src/test/resources/PDF测试.pdf"));
        MockMultipartFile file = new MockMultipartFile("file", "PDF测试.pdf", DOC.getContentType(), content);
        UploadDocDTO dto = UploadDocDTO.builder().file(file).build();
        String docId = DocApi.upload(mockMvc, operator.getToken(), dto);

        // When & Then
        DocApi.uploadRaw(mockMvc, operator.getToken(), dto)
                .expectStatus(409)
                .expectUserMessage("文档已存在");

        // Finally
        tearDownApi.removeDoc(operator.getToken(), docId);
    }

    @Test
    void should_remove_doc() throws IOException {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        String docId = DocApi.upload(mockMvc, operator.getToken(), "src/test/resources/PDF测试.pdf", PDF.getContentType());
        Doc doc = docRepository.cachedById(docId);

        RemoveDocDTO removeDocDTO = RemoveDocDTO.builder()
                .docId(docId)
                .isDeleteFile(false)
                .build();

        // When
        Boolean res = DocApi.remove(mockMvc, operator.getToken(), removeDocDTO);

        // Then
        assertTrue(res);
        assertFalse(docRepository.exists(docId));
        assertEquals(0, docRepository.getFileBytes(doc.getGridFsId()).length);
    }

    @Test
    void should_remove_doc_with_category() throws IOException {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        String docId = DocApi.upload(mockMvc, operator.getToken(), "src/test/resources/PDF测试.pdf", PDF.getContentType());
        String categoryId = CategoryApi.create(mockMvc, operator.getToken(), CategoryDTO.builder().name(rSentence(5)).build());
        CategoryApi.connect(mockMvc, operator.getToken(), ConnectDTO.builder().docId(docId).categoryId(categoryId).build());

        RemoveDocDTO removeDocDTO = RemoveDocDTO.builder()
                .docId(docId)
                .isDeleteFile(false)
                .build();

        // When
        DocApi.remove(mockMvc, operator.getToken(), removeDocDTO);

        // Then
        assertFalse(categoryRepository.exists(categoryId));
    }

}