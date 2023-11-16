/*
 * Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.example.controller;

import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

@Validated
@RestController
@RequestMapping("/api/auth")
@Tag(name = "授权管理")
public class AuthorizeController {

    @Resource
    AccountService accountService;

    @Operation(summary = "发送验证码")
    @GetMapping("/getVerifyCode")
    public Result<Void> getVerifyCode(@RequestParam("email") @Email String email,
                                      @RequestParam("type") @Pattern(regexp = "(register|reset)") String type,
                                      HttpServletRequest request) {
        return messageHandle(() -> accountService.registerEmailVerifyCode(type, email, request.getRemoteAddr()));

    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid EmailRegisterVO emailRegisterVO) {
        return messageHandle(emailRegisterVO, accountService::registerEmailAccount);
    }

    @Operation(summary = "邮箱验证(重置密码)")
    @PostMapping("/reset-confirm")
    public Result<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO confirmResetVO) {
        return messageHandle(confirmResetVO, accountService::resetConfirm);
    }

    @Operation(summary = "重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody @Valid EmailResetVO emailResetVO) {
        // 调用accountService的resetEmailAccountPassword方法重置密码
        return messageHandle(emailResetVO, accountService::resetEmailAccountPassword);
    }

    /**
     * @param vo       视图对象
     * @param function 处理视图对象的函数
     * @param <T>      泛型方法
     * @return 处理结果
     */
    private <T> Result<Void> messageHandle(T vo, Function<T, String> function) {
        return this.messageHandle(() -> function.apply(vo));
    }

    private Result<Void> messageHandle(Supplier<String> action) {
        String message = action.get();
        return message == null ? Result.success() : Result.failure(Const.STATUS_CODE_400, message);
    }
}