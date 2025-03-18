package org.ricky.core.doc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ricky.AllDocsApplication;
import org.ricky.ApiTest;
import org.ricky.core.common.domain.event.DomainEventDao;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.ricky.core.doc.domain.dto.RemoveDocDTO;
import org.ricky.core.doc.domain.event.DocCreatedEvent;
import org.ricky.core.tag.domain.TagRepository;
import org.ricky.util.SetUpApi;
import org.ricky.util.SetUpResponse;
import org.ricky.util.TearDownApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.readAllBytes;
import static org.junit.jupiter.api.Assertions.*;
import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_CREATED;
import static org.ricky.core.doc.domain.FileTypeEnum.DOC;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className DocControllerTest
 * @desc
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AllDocsApplication.class)
class DocControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    SetUpApi setUpApi;

    @Autowired
    TearDownApi tearDownApi;

    @Autowired
    DocRepository docRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    DomainEventDao domainEventDao;

    private static final String ROOT_URL = "/doc";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void should_upload_doc_with_review() throws IOException {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        byte[] content = readAllBytes(Path.of("src/test/resources/ご注文はうさぎですか165回.pdf"));
        MockMultipartFile file = new MockMultipartFile("file", "ご注文はうさぎですか165回.pdf", DOC.getContentType(), content);

        // When
        String docId = ApiTest.using(mockMvc)
                .post(ROOT_URL + "/upload")
                .bearerToken(operator.getToken())
                .file(file)
                .execute()
                .expectStatus(200)
                .as(String.class);

        // Then
        assertNotNull(docId);
        Doc doc = docRepository.cachedById(docId);
        assertEquals("ご注文はうさぎですか165回.pdf", doc.getName());
        assertNotNull(doc.getGridFsId());
        assertNotNull(doc.getTextFileId());
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
        byte[] content = readAllBytes(Path.of("src/test/resources/ご注文はうさぎですか165回.pdf"));
        MockMultipartFile file = new MockMultipartFile("file", "ご注文はうさぎですか165回.pdf", DOC.getContentType(), content);
        String docId = ApiTest.using(mockMvc)
                .post(ROOT_URL + "/upload")
                .bearerToken(operator.getToken())
                .file(file)
                .execute()
                .as(String.class);

        // When & Then
        ApiTest.using(mockMvc)
                .post(ROOT_URL + "/upload")
                .bearerToken(operator.getToken())
                .file(file)
                .execute()
                .expectStatus(409)
                .expectUserMessage("文档已存在");

        // Finally
        tearDownApi.removeDoc(operator.getToken(), docId);
    }

    @Test
    void should_remove_doc() throws IOException {
        // Given
        SetUpResponse operator = setUpApi.registryWithLogin();
        byte[] content = readAllBytes(Path.of("src/test/resources/ご注文はうさぎですか165回.pdf"));
        MockMultipartFile file = new MockMultipartFile("file", "ご注文はうさぎですか165回.pdf", DOC.getContentType(), content);
        String docId = ApiTest.using(mockMvc)
                .post(ROOT_URL + "/upload")
                .bearerToken(operator.getToken())
                .file(file)
                .execute()
                .as(String.class);
        String gridFsId = docRepository.cachedById(docId).getGridFsId();

        // When
        ApiTest.using(mockMvc)
                .delete(ROOT_URL)
                .bearerToken(operator.getToken())
                .body(RemoveDocDTO.builder()
                        .docId(docId)
                        .isDeleteFile(true)
                        .build())
                .execute()
                .expectStatus(200)
                .expectSuccess();

        // Then
        assertFalse(docRepository.exists(docId));
        assertEquals(0, docRepository.getFileBytes(gridFsId).length);

        // Finally
        tearDownApi.removeDoc(operator.getToken(), docId);
    }

}