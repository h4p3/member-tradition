package com.h4p3.service;

import com.alibaba.fastjson2.JSON;
import com.h4p3.entity.LoginEntity;
import com.h4p3.entity.MemberCacheEntity;
import com.h4p3.exception.ServiceException;
import com.h4p3.util.JwtUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
        return "hi handsome";
    }

    public String login(LoginEntity loginEntity) {
        String userName = loginEntity.userName();
        String pwd = loginEntity.pwd();

        String trulyPwd = stringRedisTemplate.opsForSet().pop(userName);
        if (trulyPwd == null || "".equals(trulyPwd)) {
            throw new ServiceException("查无此人");
        }

        // TODO: 2023/5/26 未MD5 等处理
        if (pwd.equals(trulyPwd)) {
            // TODO: 2023/5/26 查询该用户的信息，存入redis
            Map<String, Object> map = new HashMap<>();
            map.put("login_user_key", userName);
            String token = JwtUtil.createToken(map);

            MemberCacheEntity doTheBest = new MemberCacheEntity(userName, "do the best", System.currentTimeMillis());

            stringRedisTemplate.opsForValue().set(userName, JSON.toJSONString(doTheBest));
            stringRedisTemplate.opsForValue().set("token:" + userName, token, 30, TimeUnit.MINUTES);
            return token;
        }
        throw new ServiceException("密码不正确");
    }

}
