package com.example.controller;

import com.example.entity.dto.Account;
import com.example.service.AccountService;
import com.example.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private AccountService accountService;

    @Operation(summary = "获取所有用户信息")
    @GetMapping("/all")
    public Result<List<Account>> info() {
        List<Account> accounts = accountService.getAccounts();
        return Result.success(accounts);
    }

//    @PostMapping("/add")
//    public Result<Integer> add(@RequestParam Account account) {
//        Integer status = accountService.addAccount(account);
//        return Result.success(status);
//    }
}
