package org.ricky.common.auth;

import lombok.RequiredArgsConstructor;
import org.ricky.core.user.domain.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className AuthConfig
 * @desc
 */
@Configuration
@RequiredArgsConstructor
public class AuthConfig extends WebMvcConfigurationSupport {

    private final UserRepository userRepository;

    /**
     * 白名单
     */
    private static final String[] EXCLUDE_PATTERNS = {
            "/user/registry",
            "/user/login"
    };

    /**
     * 注册拦截器
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 注册TestInterceptor拦截器
        registry.addInterceptor(new AuthInterceptor(userRepository))
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATTERNS);
    }

    /**
     * 解决swagger UI页面 和 拦截器的冲突
     *
     * @param registry ResourceHandlerRegistry
     * @see <a href="https://blog.csdn.net/m0_62943596/article/details/126186521">参考链接</a>
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html", "doc.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }

}
