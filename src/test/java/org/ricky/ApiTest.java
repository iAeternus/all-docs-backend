package org.ricky;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.ricky.common.constants.ConfigConstant.AUTHORIZATION;
import static org.ricky.common.constants.ConfigConstant.BEARER;
import static org.springframework.http.HttpMethod.*;

public class ApiTest {
    private final MockMvc mockMvc;
    private HttpMethod httpMethod;
    private String url;
    private Object requestBody;
    private Object[] uriVariables = new Object[0];
    private MediaType contentType = MediaType.APPLICATION_JSON;
    private MediaType accept = MediaType.APPLICATION_JSON;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> params = new HashMap<>();
    private Charset charset = UTF_8;

    private ApiTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public static ApiTest using(MockMvc mockMvc) {
        return new ApiTest(mockMvc);
    }

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

    public ApiTest method(HttpMethod method) {
        this.httpMethod = method;
        return this;
    }

    public ApiTest url(String url) {
        this.url = url;
        return this;
    }

    public ApiTest uriVariables(Object... uriVariables) {
        this.uriVariables = uriVariables;
        return this;
    }

    public ApiTest body(Object requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public ApiTest contentType(MediaType contentType) {
        this.contentType = contentType;
        return this;
    }

    public ApiTest accept(MediaType accept) {
        this.accept = accept;
        return this;
    }

    public ApiTest header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public ApiTest bearerToken(String token) {
        headers.put(AUTHORIZATION, BEARER + token);
        return this;
    }

    public ApiTest param(String name, String value) {
        params.put(name, value);
        return this;
    }

    public ApiTest charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public ResponseExecutor execute() {
        try {
            MockHttpServletRequestBuilder requestBuilder = createRequestBuilder()
                    .contentType(contentType)
                    .accept(accept)
                    .characterEncoding(charset.name());

            headers.forEach(requestBuilder::header);
            params.forEach(requestBuilder::param);

            if (requestBody != null) {
                String content = requestBody instanceof String ?
                        (String) requestBody :
                        JSON.toJSONString(requestBody);
                requestBuilder.content(content);
            }
            MvcResult result = mockMvc.perform(requestBuilder).andReturn();
            return new ResponseExecutor(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MockHttpServletRequestBuilder createRequestBuilder() {
        if (GET.equals(httpMethod)) {
            return MockMvcRequestBuilders.get(url, uriVariables);
        } else if (POST.equals(httpMethod)) {
            return MockMvcRequestBuilders.post(url, uriVariables);
        } else if (PUT.equals(httpMethod)) {
            return MockMvcRequestBuilders.put(url, uriVariables);
        } else if (DELETE.equals(httpMethod)) {
            return MockMvcRequestBuilders.delete(url, uriVariables);
        } else if (PATCH.equals(httpMethod)) {
            return MockMvcRequestBuilders.patch(url, uriVariables);
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        }
    }

    public static class ResponseExecutor {
        private final MockHttpServletResponse response;
        private final String responseContent;
        private final JSONObject jsonBody;

        public ResponseExecutor(MvcResult result) {
            this.response = result.getResponse();
            try {
                this.responseContent = response.getContentAsString(UTF_8);
                this.jsonBody = responseContent.isEmpty() ? new JSONObject() : JSON.parseObject(responseContent);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse response body", e);
            }
        }

        /**
         * 校验HTTP状态码（保留的断言方法）
         *
         * @param expectedStatus 预期的HTTP状态码
         * @return 当前响应处理器实例（保持链式调用）
         */
        public ResponseExecutor expectStatus(int expectedStatus) {
            assertEquals(expectedStatus, response.getStatus());
            return this;
        }

        public ResponseExecutor expectUserMessage(String expectMessage) {
            Object value = jsonBody.get("userMessage");
            assertEquals(expectMessage, value);
            return this;
        }

        public ResponseExecutor expectSuccess() {
            Object data = jsonBody.get("data");
            assertTrue((Boolean) data);
            return this;
        }

        public ResponseExecutor expectFail() {
            Object data = jsonBody.get("data");
            assertFalse((Boolean) data);
            return this;
        }

        /**
         * 将响应内容中的data字段反序列化为指定类型的对象
         *
         * @param clazz data字段对应的目标类型Class
         * @return data字段反序列化后的对象实例
         */
        public <T> T as(Class<T> clazz) {
            try {
                Object data = jsonBody.get("data");
                if (data == null) {
                    throw new RuntimeException("Response body does not contain 'data' field");
                }
                return JSON.parseObject(JSON.toJSONString(data), clazz);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize data field to " + clazz.getSimpleName(), e);
            }
        }

        // 保留其他获取元数据的方法
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