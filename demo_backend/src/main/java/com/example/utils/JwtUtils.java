package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JwtUtils {

    public static final String TOKEN_PREFIX = "Bearer ";
    @Resource
    StringRedisTemplate stringRedisTemplate;
    // 获取jwt的秘钥
    @Value("${spring.security.jwt.secret}")
    private String key;
    // 获取jwt的过期时间
    @Value("${spring.security.jwt.expire}")
    private Integer expire;

    public boolean invalidJwt(String headerToken) {
        // 将headerToken转换为token
        String token = this.convertToken(headerToken);
//        log.warn("token:{}", token);
        // 如果token为空，则返回false
        if (token == null) return false;
        // 使用key和算法构建JWTVerifier
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            // 验证token
            DecodedJWT decodedJWT = verifier.verify(token);
            // 获取token中的id
            String id = decodedJWT.getId();
//            log.warn("id:{}", id);
            // 删除token
            return deleteToken(id, decodedJWT.getExpiresAt());
        } catch (JWTVerificationException e) {
            // 如果验证失败，则返回false
            return false;
        }
    }

    private boolean deleteToken(String uuid, Date time) {
        // 判断uuid是否有效
        if (this.isInValidToken(uuid)) return false;
        // 获取当前时间
        Date now = new Date();
        // 计算过期时间
        long expire = Math.max(time.getTime() - now.getTime(), 0);
        // 将uuid和过期时间存储到redis中
        stringRedisTemplate.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);
        return true;
    }

    //判断uuid是否在黑名单中
    private boolean isInValidToken(String uuid) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid));
    }

    // 生成jwt
    public String createJwt(UserDetails user, int id, String username) {
        // 创建jwt的算法
        Algorithm algorithm = Algorithm.HMAC256(key);
        // 获取jwt的过期时间
        Date expire = this.expireTime();
        // 创建jwt
        return JWT.create()
                // 添加jwt的id
                .withJWTId(UUID.randomUUID().toString())
                // 添加id
                .withClaim("id", id)
                // 添加用户名
                .withClaim("name", username)
                // 添加权限
                .withClaim("authorities", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                // 添加过期时间
                .withExpiresAt(expire)
                // 添加创建时间
                .withIssuedAt(new Date())
                // 签名
                .sign(algorithm);
    }

    public DecodedJWT parseJwt(String headToken) {
        String token = this.convertToken(headToken);
        if (token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = verifier.verify(token);
            if (this.isInValidToken(verify.getId()))
                return null;
            Date expireAt = verify.getExpiresAt();
            return new Date().after(expireAt) ? null : verify;
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claimMap = jwt.getClaims();
        return User
                // 添加用户名
                .withUsername(claimMap.get("name").asString())
                // 添加密码
                .password("******")
                // 添加权限
                .authorities(claimMap.get("authorities").asArray(String.class))
                // 构建
                .build();
    }

    public Integer toId(DecodedJWT jwt) {
        return jwt.getClaim("id").asInt();
    }


    public String toName(DecodedJWT jwt) {
        return jwt.getClaim("name").asString();
    }

    // 获取过期时间
    public Date expireTime() {
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        // 添加指定小时数
        calendar.add(Calendar.HOUR, expire * 24);
        // 返回过期时间
        return calendar.getTime();
    }

    public String convertToken(String headToken) {
        if (headToken == null || !headToken.startsWith(TOKEN_PREFIX))
            return null;
        else
            return headToken.substring(7);
    }

    public List<String> toAuthorities(DecodedJWT jwt) {
        return jwt.getClaim("authorities").asList(String.class);
    }
}