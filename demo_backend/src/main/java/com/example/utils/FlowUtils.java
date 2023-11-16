/*
 * Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FlowUtils {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * @param ipKey     限流IP键名
     * @param blockTime 限流时间
     * @return 是否进行限流
     */
    public boolean limitOnceCheck(String ipKey, int blockTime) {
        // 检查ip是否存在
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(ipKey)))
            return false;
        else {
            // 设置ip，并设置限制请求时间
            stringRedisTemplate.opsForValue().set(ipKey, "", blockTime, TimeUnit.SECONDS);
            return true;
        }
    }
}