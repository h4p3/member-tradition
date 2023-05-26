package com.h4p3.interceptor;

import com.alibaba.fastjson2.JSON;
import com.h4p3.annotation.Auth;
import com.h4p3.constant.MemberConstants;
import com.h4p3.context.RequestContext;
import com.h4p3.context.RequestContextHolder;
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
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 认证拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {


    private final StringRedisTemplate stringRedisTemplate;

    public AuthInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 先校验token
        String token = JwtUtil.getToken(request);
        if (token == null || "".equals(token)) {
            throw new AccessDeniedException("请登录");
        }

        Claims claims = JwtUtil.parseToken(token);
        String userName = (String) claims.get(MemberConstants.LOGIN_KEY);
        String userJson = stringRedisTemplate.opsForValue().get(userName);
        if (userJson == null || "".equals(userJson)) {
            throw new AccessDeniedException("请登录");
        }

        MemberCacheEntity memberCacheEntity = JSON.parseObject(userJson, MemberCacheEntity.class);

        // token即将过期，刷新token
        long tokenCreateTime = memberCacheEntity.tokenCreateTime();
        long now = System.currentTimeMillis();
        if (now - tokenCreateTime <= MemberConstants.MILLIS_MINUTE_TEN) {
            stringRedisTemplate.opsForValue().set(userName, userJson, MemberConstants.LOGIN_EXPIRE_TIME, TimeUnit.MINUTES);
        }

        // 设置上下文
        RequestContextHolder.setContext(new RequestContext(userName, memberCacheEntity.permission()));

        if (handler instanceof HandlerMethod) {
            // 校验权限
            Method method = ((HandlerMethod) handler).getMethod();
            Auth annotation = method.getAnnotation(Auth.class);
            if (annotation == null) {
                return true;
            }
            String permission = annotation.value();
            if (permission == null || "".equals(permission)) {
                return true;
            }

            List<String> userPermissions = memberCacheEntity.permission();
            if (!userPermissions.contains(permission)) {
                throw new AccessDeniedException("无权限访问");
            }
        }
        // 非视图方法，直接跳过
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 清除请求上下文
        RequestContextHolder.clearContext();
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
