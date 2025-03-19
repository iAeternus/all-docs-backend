package org.ricky.core.category;

import org.ricky.ApiTest;
import org.ricky.core.category.domain.Category;
import org.ricky.core.category.domain.dto.CategoryDTO;
import org.ricky.core.category.domain.dto.ConnectDTO;
import org.ricky.core.category.domain.dto.DisConnectDTO;
import org.ricky.core.category.domain.vo.CategoryVO;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryApi
 * @desc
 */
public class CategoryApi {

    private static final String ROOT_URL = "/category";

    public static ApiTest.ResponseExecutor createRaw(MockMvc mockMvc, String token, CategoryDTO dto) {
        return ApiTest.using(mockMvc)
                .post(ROOT_URL)
                .bearerToken(token)
                .body(dto)
                .execute();
    }

    public static String create(MockMvc mockMvc, String token, CategoryDTO dto) {
        return createRaw(mockMvc, token, dto)
                .expectStatus(200)
                .as(String.class);
    }

    public static ApiTest.ResponseExecutor getByIdRaw(MockMvc mockMvc, String token, String categoryId) {
        return ApiTest.using(mockMvc)
                .get(ROOT_URL + "/{categoryId}", categoryId)
                .bearerToken(token)
                .execute();
    }

    public static CategoryVO getById(MockMvc mockMvc, String token, String categoryId) {
        return getByIdRaw(mockMvc, token, categoryId)
                .expectStatus(200)
                .as(CategoryVO.class);
    }

    public static ApiTest.ResponseExecutor connectRaw(MockMvc mockMvc, String token, ConnectDTO dto) {
        return ApiTest.using(mockMvc)
                .put(ROOT_URL + "/connect")
                .bearerToken(token)
                .body(dto)
                .execute();
    }

    public static Boolean connect(MockMvc mockMvc, String token, ConnectDTO dto) {
        return connectRaw(mockMvc, token, dto)
                .expectStatus(200)
                .as(Boolean.class);
    }

    public static ApiTest.ResponseExecutor disconnectRaw(MockMvc mockMvc, String token, DisConnectDTO dto) {
        return ApiTest.using(mockMvc)
                .put(ROOT_URL + "/disconnect")
                .bearerToken(token)
                .body(dto)
                .execute();
    }

    public static Boolean disconnect(MockMvc mockMvc, String token, DisConnectDTO dto) {
        return disconnectRaw(mockMvc, token, dto)
                .expectStatus(200)
                .as(Boolean.class);
    }

}
