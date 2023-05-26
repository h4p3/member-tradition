package com.h4p3;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
public class MemberTraditionApplication {
    private final StringRedisTemplate stringRedisTemplate;

    public MemberTraditionApplication(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostConstruct
    public void init() {
        // 模拟数据库记录
        stringRedisTemplate.opsForHash().put("USERS", "Josh", "123456");
        stringRedisTemplate.opsForHash().put("USERS", "Jack", "741258");
        stringRedisTemplate.opsForHash().put("USERS", "Neal", "987654");
    }

    public static void main(String[] args) {
        SpringApplication.run(MemberTraditionApplication.class, args);
    }

}
