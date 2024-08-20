package com.won.myongjiCamp.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RedisInitializer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void clearRedis() {
        // 모든 데이터 삭제
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        System.out.println("Redis 데이터가 삭제되었습니다.");
    }
}