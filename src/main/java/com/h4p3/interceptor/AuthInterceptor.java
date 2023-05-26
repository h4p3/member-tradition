package com.h4p3.interceptor;

import com.alibaba.fastjson2.JSON;
import com.h4p3.annotation.Auth;
import com.h4p3.entity.MemberCacheEntity;
import com.h4p3.exception.AccessDeniedException;
import com.h4p3.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * 认证拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;
    private final StringRedisTemplate stringRedisTemplate;

    public AuthInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // TODO: 2023/5/26 先校验token
        String token = request.getHeader("Auth");
        if (token == null || "".equals(token)) {
            throw new AccessDeniedException("请登录");
        }
        Claims claims = JwtUtil.parseToken(token);
        Object userName = claims.get("login_user_key");
        String userJson = stringRedisTemplate.opsForValue().get(userName);
        if (userJson == null || "".equals(userJson)) {
            throw new AccessDeniedException("请登录");
        }
        MemberCacheEntity memberCacheEntity = JSON.parseObject(userJson, MemberCacheEntity.class);
        long l = memberCacheEntity.tokenExpire();
        long l1 = System.currentTimeMillis();
        if (l1 - l <= MILLIS_MINUTE_TEN) {
            // 刷新token

        }

        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            Auth annotation = method.getAnnotation(Auth.class);
            if (annotation == null) {
                return true;
            }
            String permission = annotation.value();
            if (permission == null || "".equals(permission)) {
                return true;
            }

            String requestURI = request.getRequestURI();
            Object o = stringRedisTemplate.opsForHash().get(permission, requestURI);
            if (o == null) {
                throw new AccessDeniedException(requestURI);
            }
        }
        return true;
    }
}
