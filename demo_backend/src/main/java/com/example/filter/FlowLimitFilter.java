/*
 * Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.example.filter;

import com.example.utils.Const;
import com.example.utils.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Order(Const.ORDER_LIMIT)
public class FlowLimitFilter extends HttpFilter {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        if (this.limitFlowByIp(ip))
            chain.doFilter(request, response);
        else {
            this.writeBlockMessage(response);
        }
    }

    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.forbidden("操作频繁，请稍后再试").toJson());
    }

    /**
     * 根据IP进行限流，同一IP三秒内超出10次拉入限流名单30秒
     *
     * @param ip 限流IP
     * @return 是否限流布尔值，为真则通过，为假则限制访问
     */
    private boolean limitFlowByIp(String ip) {
        synchronized (ip.intern()) {
            String countKey = Const.FLOW_LIMIT_COUNT + ip;
            String blockKey = Const.FLOW_LIMIT_BLOCK + ip;
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(blockKey))) {
                return false;
            } else {
//            ip限流检查
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(countKey))) {
                    long increment = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(countKey)).orElse(0L);
                    if (increment > 10) {
                        stringRedisTemplate.opsForValue().set(blockKey, "", 30, TimeUnit.SECONDS);
                        return false;
                    }
                } else {
//                3s内连续请求将计数请求次数进行限流
                    stringRedisTemplate.opsForValue().set(countKey, "1", 3, TimeUnit.SECONDS);
                }
                return true;
            }
        }
    }

}
