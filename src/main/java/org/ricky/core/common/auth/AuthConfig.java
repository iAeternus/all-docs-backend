package org.ricky.core.common.auth;

import org.ricky.core.user.domain.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthConfig implements WebMvcConfigurer {

    private final UserRepository userRepository;

    public AuthConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 白名单
     */
    private static final String[] EXCLUDE_PATTERNS = {
            "/user/registry",
            "/user/login",
            "/user/login/state",
    };

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册AuthInterceptor拦截器
        registry.addInterceptor(new AuthInterceptor(userRepository))
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATTERNS);
    }

    /**
     * 解决swagger UI页面 和 拦截器的冲突
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html", "doc.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
    }
}