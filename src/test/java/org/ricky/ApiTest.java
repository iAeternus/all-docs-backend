package org.ricky;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.springframework.http.HttpMethod.*;

/**
 * API测试工具类，提供链式调用的HTTP请求构建和响应验证功能
 *
 * <h3>Example</h3>
 * <pre>{@code
 * // 基本请求验证
 * ApiTest.using(mockMvc)
 *     .post("/api/test")
 *     .body(new Data("aaa", 123))
 *     .execute()
 *     .expectCode(200)
 *     .expectData("SUCCESS");
 *
 * // 带路径参数和查询参数
 * ApiTest.using(mockMvc)
 *     .get("/api/test/{id}", 123)
 *     .param("detail", "true")
 *     .execute()
 *     .expectStatus(200)
 *     .assertJson("data.username").isEqualTo("Ricky");
 *
 * // 自定义响应断言
 * ApiTest.using(mockMvc)
 *     .get("/api/test")
 *     .execute()
 *     .assertThat(res -> {
 *         Assertions.assertTrue(res.getContentAsString().contains("iPhone"));
 *         Assertions.assertEquals("application/json", res.getContentType());
 *     });
 * }</pre>
 */
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
    private Charset charset = StandardCharsets.UTF_8;


    private ApiTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * 创建基于给定MockMvc实例的测试构建器
     *
     * @param mockMvc Spring MVC测试框架的Mock实例
     * @return 配置好的ApiTest构建器
     */
    public static ApiTest using(MockMvc mockMvc) {
        return new ApiTest(mockMvc);
    }

    /**
     * 配置GET请求
     *
     * @param urlTemplate  请求URL模板（支持路径参数）
     * @param uriVariables 路径参数值
     * @return 当前构建器实例
     * @see #uriVariables(Object...)
     */
    public ApiTest get(String urlTemplate, Object... uriVariables) {
        return prepareRequest(GET, urlTemplate, uriVariables);
    }

    /**
     * 配置POST请求
     *
     * @param urlTemplate  请求URL模板（支持路径参数）
     * @param uriVariables 路径参数值
     * @return 当前构建器实例
     */
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

    /**
     * 设置自定义HTTP方法
     *
     * @param method HTTP方法枚举值
     * @return 当前构建器实例
     * @throws IllegalArgumentException 如果是不支持的HTTP方法
     */
    public ApiTest method(HttpMethod method) {
        this.httpMethod = method;
        return this;
    }

    /**
     * 设置请求URL（覆盖通过HTTP方法设置的URL）
     *
     * @param url 完整的请求URL
     * @return 当前构建器实例
     */
    public ApiTest url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 设置路径参数（需在URL中使用{param}占位符）
     *
     * @param uriVariables 路径参数值，按顺序替换占位符
     * @return 当前构建器实例
     */
    public ApiTest uriVariables(Object... uriVariables) {
        this.uriVariables = uriVariables;
        return this;
    }

    /**
     * 设置请求正文（自动JSON序列化）
     *
     * @param requestBody 请求体对象，支持String或POJO
     * @return 当前构建器实例
     */
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

    public ApiTest param(String name, String value) {
        params.put(name, value);
        return this;
    }

    public ApiTest charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 执行配置的HTTP请求
     *
     * @return 响应处理器，用于进行断言验证
     * @throws Exception 如果请求执行失败
     */
    public ResponseExecutor execute() throws Exception {
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
    }

    private MockHttpServletRequestBuilder createRequestBuilder() {
        if (GET.equals(httpMethod)) {
            return MockMvcRequestBuilders.get(url);
        } else if (POST.equals(httpMethod)) {
            return MockMvcRequestBuilders.post(url);
        } else if (PUT.equals(httpMethod)) {
            return MockMvcRequestBuilders.put(url);
        } else if (DELETE.equals(httpMethod)) {
            return MockMvcRequestBuilders.delete(url);
        } else if (PATCH.equals(httpMethod)) {
            return MockMvcRequestBuilders.patch(url);
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        }
    }

    /**
     * 响应处理器，提供多种断言验证方法
     *
     * <h3>Example</h3>
     * <pre>{@code
     * .execute()
     *   .expectStatus(200)
     *   .expectCode(200)
     *   .assertJson("data.age").inRange(18, 60)
     *   .assertJson("data.email").matches("\\w+@\\w+\\.com")
     * }</pre>
     */
    public static class ResponseExecutor {
        private final MockHttpServletResponse response;
        private final JSONObject jsonBody;

        public ResponseExecutor(MvcResult result) {
            this.response = result.getResponse();
            try {
                String content = response.getContentAsString();
                this.jsonBody = content.isEmpty() ? new JSONObject() : JSON.parseObject(content);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse response body", e);
            }
        }

        /**
         * 验证HTTP状态码
         *
         * @param expected 期望的状态码（如200、404等）
         * @return 当前响应处理器实例
         */
        public ResponseExecutor expectStatus(int expected) {
            return assertThat(res -> Assertions.assertEquals(expected, res.getStatus()));
        }


        /**
         * 验证响应JSON中的code字段值
         *
         * @param expected 期望的code值
         * @return 当前响应处理器实例
         */
        public ResponseExecutor expectCode(int expected) {
            return assertJson("code", code -> Assertions.assertEquals(expected, code));
        }

        /**
         * 验证响应JSON中的data字段值
         *
         * @param expected 期望的data值
         * @return 当前响应处理器实例
         */
        public ResponseExecutor expectData(Object expected) {
            return assertJson("data", data -> Assertions.assertEquals(expected, data));
        }

        /**
         * 创建JSON路径断言器
         *
         * @param jsonPath JSON路径表达式（如"data.username"）
         * @return Json断言构建器
         * @example {@code
         * .assertJson("data.items[0].price").isEqualTo(199.9)
         * }
         */
        public JsonAssert assertJson(String jsonPath) {
            Object value = getJsonValue(jsonPath);
            return new JsonAssert(this, jsonPath, value);
        }

        /**
         * 自定义响应断言
         *
         * @param assertion 接收MockHttpServletResponse的断言逻辑
         * @return 当前响应处理器实例
         */
        public ResponseExecutor assertThat(Consumer<MockHttpServletResponse> assertion) {
            assertion.accept(response);
            return this;
        }

        /**
         * 自定义JSON字段断言
         *
         * @param jsonPath  JSON路径表达式
         * @param assertion 接收字段值的断言逻辑
         * @return 当前响应处理器实例
         * @example {@code
         * .assertJson("data.roles", roles -> Assertions.assertTrue(roles.toString().contains("ADMIN"))
         * }
         */
        public ResponseExecutor assertJson(String jsonPath, Consumer<Object> assertion) {
            Object value = getJsonValue(jsonPath);
            assertion.accept(value);
            return this;
        }

        private Object getJsonValue(String jsonPath) {
            Object value = jsonBody.get(jsonPath);
            Assertions.assertNotNull(value, "JSON路径不存在: " + jsonPath);
            return value;
        }

        /**
         * JSON字段断言构建器
         */
        public static class JsonAssert {
            private final ResponseExecutor executor;
            private final String jsonPath;
            private final Object value;


            JsonAssert(ResponseExecutor executor, String jsonPath, Object value) {
                this.executor = executor;
                this.jsonPath = jsonPath;
                this.value = value;
            }

            /**
             * 验证字段等于指定值
             *
             * @param expected 期望值
             * @return 上级响应处理器
             */
            public ResponseExecutor isEqualTo(Object expected) {
                Assertions.assertEquals(expected, value, jsonPath + " 值不匹配");
                return executor;
            }

            /**
             * 验证字段不等于指定值
             *
             * @param expected 期望值
             * @return 上级响应处理器
             */
            public ResponseExecutor isNotEqualTo(Object expected) {
                Assertions.assertNotEquals(expected, value, jsonPath + " 值不应相等");
                return executor;
            }

            /**
             * 验证字段为空
             *
             * @return 上级响应处理器
             */
            public ResponseExecutor isNull() {
                Assertions.assertNull(value, jsonPath + " 应为null");
                return executor;
            }

            /**
             * 验证字段不为空
             *
             * @return 上级响应处理器
             */
            public ResponseExecutor isNotNull() {
                Assertions.assertNotNull(value, jsonPath + " 不应为null");
                return executor;
            }

            /**
             * 验证字段数值在指定范围内
             *
             * @param min 最小值（包含）
             * @param max 最大值（包含）
             * @return 上级响应处理器
             * @throws AssertionError 如果字段不是数值类型或超出范围
             */
            public ResponseExecutor inRange(Number min, Number max) {
                Assertions.assertInstanceOf(Number.class, value, jsonPath + " 不是数值类型");
                double actual = ((Number) value).doubleValue();
                Assertions.assertTrue(actual >= min.doubleValue() && actual <= max.doubleValue(),
                        () -> String.format("%s 超出范围 [%s, %s]，实际值：%s", jsonPath, min, max, actual));
                return executor;
            }

            public ResponseExecutor contains(String substring) {
                Assertions.assertTrue(value.toString().contains(substring),
                        () -> String.format("%s 不包含子字符串，内容：%s", jsonPath, value));
                return executor;
            }

            public ResponseExecutor matches(String regex) {
                Assertions.assertTrue(value.toString().matches(regex),
                        () -> String.format("%s 不匹配正则表达式，内容：%s", jsonPath, value));
                return executor;
            }
        }
    }
}
