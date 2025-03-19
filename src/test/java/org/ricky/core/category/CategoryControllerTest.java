package org.ricky.core.category;

import org.junit.jupiter.api.Test;
import org.ricky.BaseApiTest;
import org.ricky.core.category.domain.Category;
import org.ricky.core.category.domain.dto.CategoryDTO;
import org.ricky.core.category.domain.dto.ConnectDTO;
import org.ricky.core.category.domain.dto.DisConnectDTO;
import org.ricky.core.category.domain.vo.CategoryVO;
import org.ricky.core.doc.DocApi;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.dto.UploadDocDTO;
import org.ricky.util.SetUpResponse;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.readAllBytes;
import static org.junit.jupiter.api.Assertions.*;
import static org.ricky.core.doc.domain.FileTypeEnum.DOC;
import static org.ricky.core.doc.domain.FileTypeEnum.PDF;
import static org.ricky.util.RandomTestFixture.rSentence;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryControllerTest
 * @desc
 */
class CategoryControllerTest extends BaseApiTest {

    @Test
    void should_create_category() {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        String categoryName = rSentence(5);
        CategoryDTO dto = CategoryDTO.builder().name(categoryName).build();

        // When
        String categoryId = CategoryApi.create(mockMvc, operator.getToken(), dto);

        // Then
        Category category = categoryRepository.byId(categoryId);
        assertEquals(categoryName, category.getName());
    }

    @Test
    void should_get_by_id() {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        String categoryName = rSentence(5);
        CategoryDTO dto = CategoryDTO.builder().name(categoryName).build();
        String categoryId = CategoryApi.create(mockMvc, operator.getToken(), dto);

        // When
        CategoryVO categoryVO = CategoryApi.getById(mockMvc, operator.getToken(), categoryId);

        // Then
        assertEquals(categoryId, categoryVO.getId());
        assertEquals(categoryName, categoryVO.getName());
    }

    @Test
    void should_connect_doc_with_category() throws IOException {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        String categoryId = CategoryApi.create(mockMvc, operator.getToken(), CategoryDTO.builder().name(rSentence(5)).build());
        String docId = DocApi.upload(mockMvc, operator.getToken(), "src/test/resources/PDF测试.pdf", PDF.getContentType());

        ConnectDTO categoryDTO = ConnectDTO.builder()
                .docId(docId)
                .categoryId(categoryId)
                .build();

        // When
        Boolean res = CategoryApi.connect(mockMvc, operator.getToken(), categoryDTO);

        // Then
        assertTrue(res);

        Doc doc = docRepository.cachedById(docId);
        assertEquals(categoryId, doc.getCategoryId());

        // Finally
        tearDownApi.removeDoc(operator.getToken(), docId);
    }

    @Test
    void should_disconnect_doc_with_category() throws IOException {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        String categoryId = CategoryApi.create(mockMvc, operator.getToken(), CategoryDTO.builder().name(rSentence(5)).build());
        String docId = DocApi.upload(mockMvc, operator.getToken(), "src/test/resources/PDF测试.pdf", PDF.getContentType());
        CategoryApi.connect(mockMvc, operator.getToken(), ConnectDTO.builder().docId(docId).categoryId(categoryId).build());

        DisConnectDTO disConnectDTO = DisConnectDTO.builder()
                .docId(docId)
                .categoryId(categoryId)
                .build();

        // When
        Boolean res = CategoryApi.disconnect(mockMvc, operator.getToken(), disConnectDTO);

        // Then
        assertTrue(res);
        assertFalse(categoryRepository.exists(categoryId));

        // Finally
        tearDownApi.removeDoc(operator.getToken(), docId);
    }

}