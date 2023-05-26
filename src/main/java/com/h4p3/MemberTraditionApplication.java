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
        stringRedisTemplate.opsForHash().put("READ", "/mt/member/sayHi", ".");
        stringRedisTemplate.opsForHash().put("READ", "/mt/member/sayHello", ".");
        stringRedisTemplate.opsForHash().put("READ", "/mt/member/sayYeah", ".");
        stringRedisTemplate.opsForHash().put("READ", "/mt/member/sayNothing", ".");
    }

    public static void main(String[] args) {
        SpringApplication.run(MemberTraditionApplication.class, args);
    }

}
