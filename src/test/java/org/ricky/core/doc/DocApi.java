package org.ricky.core.doc;

import org.ricky.ApiTest;
import org.ricky.core.common.domain.page.PageVO;
import org.ricky.core.doc.domain.dto.DocPageDTO;
import org.ricky.core.doc.domain.dto.RemoveDocDTO;
import org.ricky.core.doc.domain.dto.UploadDocDTO;
import org.ricky.core.doc.domain.vo.DocVO;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.readAllBytes;
import static org.ricky.core.common.util.FileUtil.getLastPart;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className DocApi
 * @desc
 */
public class DocApi {

    private static final String ROOT_URL = "/doc";

    public static ApiTest.ResponseExecutor uploadRaw(MockMvc mockMvc, String token, UploadDocDTO dto) {
        return ApiTest.using(mockMvc)
                .post(ROOT_URL + "/upload")
                .bearerToken(token)
                .file((MockMultipartFile) dto.getFile())
                .execute();
    }

    public static String upload(MockMvc mockMvc, String token, UploadDocDTO dto) {
        return uploadRaw(mockMvc, token, dto)
                .expectStatus(200)
                .as(String.class);
    }

    public static String upload(MockMvc mockMvc, String token, String filepath, String contentType) throws IOException {
        byte[] content = readAllBytes(Path.of(filepath));
        MockMultipartFile file = new MockMultipartFile("file", getLastPart(filepath), contentType, content);
        UploadDocDTO dto = UploadDocDTO.builder().file(file).build();
        return upload(mockMvc, token, dto);
    }

    public static ApiTest.ResponseExecutor removeRaw(MockMvc mockMvc, String token, RemoveDocDTO dto) {
        return ApiTest.using(mockMvc)
                .delete(ROOT_URL)
                .bearerToken(token)
                .body(dto)
                .execute();
    }

    public static Boolean remove(MockMvc mockMvc, String token, RemoveDocDTO dto) {
        return removeRaw(mockMvc, token, dto)
                .expectStatus(200)
                .as(Boolean.class);
    }

    public static ApiTest.ResponseExecutor pageRaw(MockMvc mockMvc, String token, DocPageDTO dto) {
        return ApiTest.using(mockMvc)
                .post(ROOT_URL + "/page")
                .bearerToken(token)
                .body(dto)
                .execute();
    }

    @SuppressWarnings("unchecked")
    public static PageVO<DocVO> page(MockMvc mockMvc, String token, DocPageDTO dto) {
        return pageRaw(mockMvc, token, dto)
                .expectStatus(200)
                .as(PageVO.class);
    }

}
