package com.h4p3.service;

import com.alibaba.fastjson2.JSON;
import com.h4p3.constant.MemberConstants;
import com.h4p3.context.RequestContext;
import com.h4p3.context.RequestContextHolder;
import com.h4p3.entity.LoginEntity;
import com.h4p3.entity.MemberCacheEntity;
import com.h4p3.exception.AccessDeniedException;
import com.h4p3.util.JwtUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class MemberService {
    private final StringRedisTemplate stringRedisTemplate;

    public MemberService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String sayHi() {
        RequestContext context = RequestContextHolder.getContext();
        String userName = context.userName();
        String permissions = String.join(",", context.permissions());
        return String.format("尊敬的 %s 你好，你拥有 %s 的权限", userName, permissions);
    }

    public String login(LoginEntity loginEntity) {
        String userName = loginEntity.userName();
        String pwd = loginEntity.pwd();

        Object trulyPwd = stringRedisTemplate.opsForHash().get("USERS", userName);
        if (trulyPwd == null || "".equals(trulyPwd)) {
            throw new AccessDeniedException("查无此人");
        }

        // TODO: 2023/5/26 未MD5 等处理
        if (trulyPwd.equals(pwd)) {
            // TODO: 2023/5/26 查询该用户的信息，存入redis
            Map<String, Object> map = new HashMap<>();
            map.put(MemberConstants.LOGIN_KEY, userName);
            String token = JwtUtil.createToken(map);

            MemberCacheEntity doTheBest = new MemberCacheEntity(userName, "do the best", System.currentTimeMillis(), Arrays.asList("READ", "WRITE"));

            stringRedisTemplate.opsForValue().set(userName, JSON.toJSONString(doTheBest), MemberConstants.LOGIN_EXPIRE_TIME, TimeUnit.MINUTES);
            return token;
        }
        throw new AccessDeniedException("密码不正确");
    }

}
