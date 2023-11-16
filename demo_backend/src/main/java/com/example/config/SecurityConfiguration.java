package com.example.config;

import com.example.entity.dto.Account;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.filter.JwtAuthorizeFilter;
import com.example.service.AccountService;
import com.example.utils.JwtUtils;
import com.example.utils.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

import static com.example.utils.Const.STATUS_CODE_400;

@Slf4j
@Configuration
public class SecurityConfiguration {

    //注入jwt工具类
    @Resource
    JwtUtils jwtUtils;

    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;

    @Resource
    AccountService accountService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //配置过滤器链
        return http
                .authorizeHttpRequests(conf -> {
                    //配置请求路径，允许所有请求
                    conf.requestMatchers("/api/auth/**", "/error", "/doc.html", "/webjars/**", "/v3/api-docs/**").permitAll()
                            //其他请求需要认证
                            .anyRequest().authenticated();
                })
                .formLogin(conf -> {
                    //设置登录请求路径
                    conf.loginProcessingUrl("/api/auth/login")
                            //认证成功回调
                            .successHandler(this::onAuthenticationSuccess)
                            //认证失败回调
                            .failureHandler(this::onAuthenticationFailure);
//                            .successHandler((req, res, auth) -> res.getWriter().write("登陆成功"))
//                            .failureHandler((req, res, e) -> res.getWriter().write("登陆失败"));
                })
                .logout(conf -> {
                    //设置登出请求路径
                    conf.logoutUrl("/api/auth/logout")
                            //登出成功回调
                            .logoutSuccessHandler(this::onLogoutSuccess);
                })
                .exceptionHandling(conf -> {
                    conf.authenticationEntryPoint(this::onUnAuthorize);
                    conf.accessDeniedHandler(this::onAccessDeny);
                })
                .csrf(AbstractHttpConfigurer::disable)  //关闭csrf跨站请求
                .sessionManagement(conf -> {
                    //设置会话创建策略
                    conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS);    //无状态jwt认证
                })
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)    //将自定义的jwt认证过滤器添加在默认的过滤器之前
                .build();

    }

    private void onAccessDeny(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(Result.forbidden(e.getMessage()).toJson());
    }

    private void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        String headerToken = request.getHeader("Authorization");
//        log.warn(headerToken);
        PrintWriter writer = response.getWriter();
        if (jwtUtils.invalidJwt(headerToken))
            writer.write(Result.success("退出登录成功").toJson());
        else
            writer.write(Result.failure(STATUS_CODE_400, "退出登录失败").toJson());
    }

    public void onUnAuthorize(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(Result.unAuthorize(exception.getMessage()).toJson());
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //设置响应类型
        response.setContentType("application/json;charset=utf-8");
        //获取用户信息
        User user = (User) authentication.getPrincipal();
        Account account = accountService.getAccountByNameOrEmail(user.getUsername());//该username可能是用户名也可能是邮箱
        //生成token
        String token = jwtUtils.createJwt(user, account.getId(), account.getUsername());
        //生成授权信息
        AuthorizeVO vo = account.asViewObject(AuthorizeVO.class, v -> {
            v.setToken(token);
            v.setExpire(jwtUtils.expireTime());
        }); //类的同属性传递
        //返回授权信息
        response.getWriter().write(Result.success(vo).toJson());
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //设置响应类型
        response.setContentType("application/json;charset=utf-8");
        //返回认证失败信息
        response.getWriter().write(Result.unAuthorize(exception.getMessage()).toJson());
    }
}