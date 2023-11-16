package com.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

    @Resource
    JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取token
        String headToken = request.getHeader("Authorization");
        // 使用token解析出jwt
        DecodedJWT jwt = jwtUtils.parseJwt(headToken);
        // 如果jwt不为空，则将jwt中的用户信息转换成用户对象
        if (jwt != null) {
            UserDetails user = jwtUtils.toUser(jwt);
            // 创建一个认证令牌
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            // 设置认证令牌的详细信息
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 将认证令牌放入安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 将用户id和用户名放入请求属性中
//            request.setAttribute("id", jwtUtils.toId(jwt));
//            request.setAttribute("name", jwtUtils.toName(jwt));
//            request.setAttribute("authorities", jwtUtils.toAuthorities(jwt));
        }
        // 执行过滤器链
        filterChain.doFilter(request, response);
    }
}