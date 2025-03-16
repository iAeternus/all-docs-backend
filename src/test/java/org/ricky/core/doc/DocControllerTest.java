package org.ricky.core.doc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ricky.AllDocsApplication;
import org.ricky.ApiTest;
import org.ricky.core.common.domain.event.DomainEventDao;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.ricky.core.doc.domain.event.DocCreatedEvent;
import org.ricky.util.SetUpApi;
import org.ricky.util.SetUpResponse;
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
import static org.ricky.core.doc.domain.DocTypeEnum.DOC;

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
    DocRepository docRepository;

    @Autowired
    DomainEventDao domainEventDao;

    private static final String ROOT_URL = "/doc";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void should_upload_doc_with_review() throws IOException {
        SetUpResponse operator = setUpApi.registryWithLogin();
        byte[] content = readAllBytes(Path.of("src/test/resources/中央纪委对二十届中央纪委四次全会解读（经李希书记、刘金国书记签批，求是刊发）.doc"));
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "中央纪委对二十届中央纪委四次全会解读（经李希书记、刘金国书记签批，求是刊发）.doc",
                DOC.getContentType(),
                content
        );

        String docId = ApiTest.using(mockMvc)
                .post(ROOT_URL + "/upload")
                .bearerToken(operator.getToken())
                .file(file)
                .execute()
                .expectStatus(200)
                .as(String.class);

        assertNotNull(docId);
        Doc doc = docRepository.cachedById(docId);
        assertEquals("中央纪委对二十届中央纪委四次全会解读（经李希书记、刘金国书记签批，求是刊发）.doc", doc.getName());

        DocCreatedEvent evt = domainEventDao.latestEventFor(docId, DOC_CREATED, DocCreatedEvent.class);
        assertEquals(docId, evt.getDocId());
        assertEquals(1, evt.getConsumedCount());
    }

}