package org.ricky;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.spring.SpringApplicationContext;
import org.ricky.core.common.util.MyObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.ricky.common.constants.ConfigConstant.AUTHORIZATION;
import static org.ricky.common.constants.ConfigConstant.BEARER;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * REST API 测试工具类，支持链式调用方式构建请求并验证响应
 * <p>提供以下核心功能：</p>
 * <ul>
 *   <li>支持所有HTTP方法（GET/POST/PUT/DELETE/PATCH）</li>
 *   <li>支持文件上传（multipart/form-data）</li>
 *   <li>支持JSON/表单参数/查询参数等多种数据格式</li>
 *   <li>自动化断言响应状态码和业务字段</li>
 *   <li>响应数据自动反序列化</li>
 * </ul>
 *
 * <p>典型用法：</p>
 * <pre>{@code
 * ApiTest.using(mockMvc)
 *     .post("/api/upload")
 *     .file("file", content, "TXT测试.txt")
 *     .param("description", "示例文件")
 *     .execute()
 *     .expectStatus(200)
 *     .expectUserMessage("上传成功");
 * }</pre>
 */
@Slf4j
public class ApiTest {
    private final MockMvc mockMvc;
    private HttpMethod httpMethod;
    private String url;
    private Object requestBody;
    private Object[] uriVariables = new Object[0];
    private MediaType contentType = APPLICATION_JSON;
    private MediaType accept = APPLICATION_JSON;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> params = new HashMap<>();
    private final Map<String, MockMultipartFile> multipartFiles = new HashMap<>();
    private final MyObjectMapper objectMapper;
    private Charset charset = UTF_8;

    private ApiTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = SpringApplicationContext.getBean(MyObjectMapper.class);
    }

    /**
     * 创建测试实例的入口方法
     *
     * @param mockMvc Spring MockMvc实例
     * @return 初始化的ApiTest实例
     */
    public static ApiTest using(MockMvc mockMvc) {
        return new ApiTest(mockMvc);
    }

    // HTTP方法快捷设置

    public ApiTest get(String urlTemplate, Object... uriVariables) {
        return prepareRequest(GET, urlTemplate, uriVariables);
    }

    public ApiTest post(String urlTemplate, Object... uriVariables) {
        return prepareRequest(POST, urlTemplate, uriVariables);
    }

    public ApiTest put(String urlTemplate, Object... uriVariables) {
        return prepareRequest(PUT, urlTemplate, uriVariables);
    }

    public ApiTest delete(String urlTemplate, Object... uriVariables) {
        return prepareRequest(DELETE, urlTemplate, uriVariables);
    }

    public ApiTest patch(String urlTemplate, Object... uriVariables) {
        return prepareRequest(PATCH, urlTemplate, uriVariables);
    }

    private ApiTest prepareRequest(HttpMethod method, String urlTemplate, Object... uriVariables) {
        this.httpMethod = method;
        this.url = urlTemplate;
        this.uriVariables = uriVariables;
        return this;
    }

    // 请求参数设置方法

    /**
     * 设置自定义HTTP方法
     *
     * @param method HTTP方法枚举
     * @return 当前实例（链式调用）
     */
    public ApiTest method(HttpMethod method) {
        this.httpMethod = method;
        return this;
    }

    /**
     * 设置请求URL（可覆盖链式方法设置的URL）
     *
     * @param url 完整的请求路径
     * @return 当前实例（链式调用）
     */
    public ApiTest url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 设置URI路径变量
     *
     * @param uriVariables 路径参数数组（按顺序对应@PathVariable）
     * @return 当前实例（链式调用）
     */
    public ApiTest uriVariables(Object... uriVariables) {
        this.uriVariables = uriVariables;
        return this;
    }

    /**
     * 设置请求体（自动序列化为JSON）
     *
     * @param requestBody 请求体对象
     * @return 当前实例（链式调用）
     */
    public ApiTest body(Object requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    /**
     * 设置Content-Type请求头
     *
     * @param contentType 媒体类型
     * @return 当前实例（链式调用）
     */
    public ApiTest contentType(MediaType contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * 设置Accept请求头
     *
     * @param accept 媒体类型
     * @return 当前实例（链式调用）
     */
    public ApiTest accept(MediaType accept) {
        this.accept = accept;
        return this;
    }

    /**
     * 添加自定义请求头
     *
     * @param name  请求头名称
     * @param value 请求头值
     * @return 当前实例（链式调用）
     */
    public ApiTest header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    /**
     * 设置Bearer Token认证头
     *
     * @param token JWT令牌
     * @return 当前实例（链式调用）
     */
    public ApiTest bearerToken(String token) {
        headers.put(AUTHORIZATION, BEARER + token);
        return this;
    }

    /**
     * 添加请求参数（URL查询参数或表单字段）
     *
     * @param name  参数名
     * @param value 参数值
     * @return 当前实例（链式调用）
     */
    public ApiTest param(String name, String value) {
        params.put(name, value);
        return this;
    }

    /**
     * 设置字符编码
     *
     * @param charset 字符编码
     * @return 当前实例（链式调用）
     */
    public ApiTest charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 添加预构建的MultipartFile文件参数
     *
     * @param file MockMultipartFile实例
     * @return 当前实例（链式调用）
     */
    public ApiTest file(MockMultipartFile file) {
        multipartFiles.put(file.getName(), file);
        return this;
    }

    // 请求执行核心方法

    /**
     * 执行构建好的请求并返回响应处理器
     *
     * @return 响应处理器实例
     */
    public ResponseExecutor execute() {
        try {
            MockHttpServletRequestBuilder requestBuilder = buildRequestBase();
            configureRequest(requestBuilder);
            applyParameters(requestBuilder);
            applyRequestBody(requestBuilder);

            return new ResponseExecutor(
                    mockMvc.perform(requestBuilder).andReturn(),
                    objectMapper
            );
        } catch (Exception e) {
            throw new RuntimeException("请求执行失败", e);
        }
    }

    /**
     * 构建基础请求对象
     */
    private MockHttpServletRequestBuilder buildRequestBase() {
        return hasMultipartFiles() ?
                buildMultipartRequest() :
                buildStandardRequest();
    }

    /**
     * 配置请求公共属性
     */
    private void configureRequest(MockHttpServletRequestBuilder builder) {
        builder.contentType(contentType)
                .accept(accept)
                .characterEncoding(charset.name());
        headers.forEach(builder::header);
    }

    /**
     * 应用请求参数（查询参数或表单字段）
     */
    private void applyParameters(MockHttpServletRequestBuilder builder) {
        if (hasMultipartFiles()) {
            params.forEach((k, v) -> ((MockMultipartHttpServletRequestBuilder) builder)
                    .part(new MockPart(k, v.getBytes(StandardCharsets.UTF_8)))
            );
        } else {
            params.forEach(builder::param);
        }
    }

    /**
     * 应用请求体内容
     */
    private void applyRequestBody(MockHttpServletRequestBuilder builder) {
        if (requestBody != null && !hasMultipartFiles()) {
            try {
                builder.content(requestBody instanceof String ? (String) requestBody : objectMapper.writeValueAsString(requestBody));
            } catch (Exception e) {
                throw new RuntimeException("请求体序列化失败", e);
            }
        }
    }

    private boolean hasMultipartFiles() {
        return !multipartFiles.isEmpty();
    }

    private MockMultipartHttpServletRequestBuilder buildMultipartRequest() {
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart(httpMethod, url, uriVariables);
        multipartFiles.values().forEach(builder::file);
        contentType = MediaType.MULTIPART_FORM_DATA;
        return builder;
    }

    private MockHttpServletRequestBuilder buildStandardRequest() {
        return MockMvcRequestBuilders.request(httpMethod, url, uriVariables);
    }

    private void applyMultipartParameters(MockMultipartHttpServletRequestBuilder builder) {
        params.forEach((name, value) -> builder.part(new MockPart(name, value.getBytes(StandardCharsets.UTF_8))));
    }

    // 响应处理器

    /**
     * 响应结果处理器，提供链式断言方法
     */
    public static class ResponseExecutor {
        private final MyObjectMapper objectMapper;
        private final MockHttpServletResponse response;
        private final String responseContent;
        private final JsonNode jsonBody;

        private ResponseExecutor(MvcResult result, MyObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            this.response = result.getResponse();
            this.responseContent = parseResponseContent();
            this.jsonBody = parseJsonBody();
        }

        /**
         * 校验HTTP状态码
         *
         * @param expectedStatus 预期状态码
         * @return 当前实例（链式调用）
         */
        public ResponseExecutor expectStatus(int expectedStatus) {
            assertEquals(expectedStatus, response.getStatus(), "HTTP状态码不符预期");
            return this;
        }

        /**
         * 校验业务消息字段
         *
         * @param expectMessage 预期消息内容
         * @return 当前实例（链式调用）
         */
        public ResponseExecutor expectUserMessage(String expectMessage) {
            assertEquals(expectMessage, jsonBody.get("userMessage").asText(), "业务消息不符预期");
            return this;
        }

        /**
         * 校验操作成功标志（要求data字段为true）
         *
         * @return 当前实例（链式调用）
         */
        public ResponseExecutor expectSuccess() {
            assertTrue(getBooleanDataField(), "预期操作成功但返回失败");
            return this;
        }

        /**
         * 校验操作失败标志（要求data字段为false）
         *
         * @return 当前实例（链式调用）
         */
        public ResponseExecutor expectFail() {
            assertFalse(getBooleanDataField(), "预期操作失败但返回成功");
            return this;
        }

        /**
         * 反序列化data字段为指定类型
         *
         * @param clazz 目标类型Class
         * @return 反序列化后的对象
         */
        public <T> T as(Class<T> clazz) {
            try {
                Object data = getDataField();
                return objectMapper.convertValue(data, clazz);
            } catch (Exception e) {
                log.error("data字段反序列化失败，目标类型: {}", clazz.getName());
                throw new RuntimeException("data字段反序列化失败: " + clazz.getName(), e);
            }
        }

        // public <T> T as(TypeReference<T> typeRef) {
        //     try {
        //         Object data = getDataField();
        //         return objectMapper.readValue(
        //                 objectMapper.writeValueAsString(data),
        //                 typeRef
        //         );
        //     } catch (Exception e) {
        //         log.error("data字段反序列化失败，目标类型: {}", typeRef.getType());
        //         throw new RuntimeException("data字段反序列化失败: " + typeRef.getType(), e);
        //     }
        // }

        private String parseResponseContent() {
            try {
                return response.getContentAsString(UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("响应内容解析失败", e);
            }
        }

        private JsonNode parseJsonBody() {
            try {
                return responseContent.isEmpty() ?
                        objectMapper.createObjectNode() :
                        objectMapper.readTree(responseContent);
            } catch (Exception e) {
                throw new RuntimeException("JSON解析失败", e);
            }
        }

        private Object getDataField() {
            JsonNode dataNode = jsonBody.get("data");
            if (dataNode == null || dataNode.isNull()) {
                throw new RuntimeException("响应缺少data字段");
            }
            return dataNode;
        }

        private boolean getBooleanDataField() {
            BooleanNode dataField = (BooleanNode) getDataField();
            try {
                return dataField.asBoolean();
            } catch (Exception ex) {
                throw new AssertionError("data字段不是布尔类型，data=" + dataField);
            }
        }

        // 元数据获取
        public int getStatus() {
            return response.getStatus();
        }

        public String getContent() {
            return responseContent;
        }

        public String getHeader(String name) {
            return response.getHeader(name);
        }
    }
}
