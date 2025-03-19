package org.ricky.core.common.auth;

import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.ricky.core.common.context.ThreadLocalContext;
import org.ricky.core.user.domain.User;
import org.ricky.core.user.domain.UserRepository;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;
import java.util.Map;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.util.Arrays.stream;
import static org.ricky.common.constants.ConfigConstant.AUTHORIZATION;
import static org.ricky.common.constants.ConfigConstant.BEARER;
import static org.ricky.core.common.context.UserContext.of;
import static org.ricky.core.common.util.JwtUtil.verifyToken;
import static org.ricky.core.common.util.ValidationUtil.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className AuthInterceptor
 * @desc 权限校验，拦截器Interceptor, 拦截器在过滤器Filter之后
 * @see <a href="https://blog.csdn.net/LitongZero/article/details/103628706">注解式权限校验</a>
 */
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * 拦截器中无法注入bean，因此使用构造器
     */
    private final UserRepository userRepository;

    public AuthInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 从请求头中获取用户
        User userInfo = getUser(request, response);
        if (userInfo == null) {
            return false;
        }

        // 设置上下文
        ThreadLocalContext.setContext(of(userInfo.getId(), userInfo.getUsername()));

        // 获取方法中的注解
        Method method = handlerMethod.getMethod();
        Permission classAnno = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Permission.class);
        Permission methodAnno = AnnotationUtils.findAnnotation(method, Permission.class);

        // 判断是否需要权限校验
        if (noValidationRequired(classAnno, methodAnno)) {
            return true; // 不需要权限校验，直接放行
        }

        // 获取注解中的权限数组
        PermissionEnum[] permissionEnums = getPermissionEnums(classAnno, methodAnno);

        // 校验该用户是否有该权限
        if (!userInfo.checkPermission(permissionEnums)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true; // 拥有权限，放行
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler,
                           ModelAndView modelAndView) {
        // empty
    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        // 清理上下文，避免内存泄露
        ThreadLocalContext.removeContext();
    }

    /**
     * 获取注解中的权限数组
     * 优先级：方法注解 > 类注解
     */
    private static PermissionEnum[] getPermissionEnums(Permission classAnno, Permission methodAnno) {
        PermissionEnum[] permissionEnums;
        if (nonNull(classAnno) && isNull(methodAnno)) {
            permissionEnums = classAnno.name(); // 类注解不为空，方法注解为空，使用类注解
        } else if (isNull(classAnno)) {
            permissionEnums = methodAnno.name(); // 类注解为空，使用方法注解
        } else {
            permissionEnums = methodAnno.name(); // 都不为空，使用方法注解
        }
        return permissionEnums;
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        // 获取header中的token
        final String authHeader = request.getHeader(AUTHORIZATION);
        if (isNull(authHeader) || !authHeader.startsWith(BEARER)) {
            response.setStatus(SC_UNAUTHORIZED); // 401
            return null;
        }

        // 提取Bearer Token
        final String token = authHeader.substring(BEARER.length());

        Map<String, Claim> userData = verifyToken(token);
        if (isEmpty(userData)) {
            response.setStatus(SC_UNAUTHORIZED);
            return null;
        }

        User userInfo = userRepository.cachedById(userData.get("id").asString());
        if (isNull(userInfo) || isNull(userInfo.getPermission())) {
            response.setStatus(SC_UNAUTHORIZED);
            return null;
        }
        return userInfo;
    }

    /**
     * 判断是否不需要权限校验
     */
    private boolean noValidationRequired(Permission classAnno, Permission methodAnno) {
        return isNull(classAnno) && isNull(methodAnno) ||
                nonNull(methodAnno) && stream((methodAnno.name())).anyMatch(permissionEnum -> permissionEnum == PermissionEnum.NO) ||
                nonNull(classAnno) && stream((classAnno.name())).anyMatch(permissionEnum -> permissionEnum == PermissionEnum.NO);
    }

}
