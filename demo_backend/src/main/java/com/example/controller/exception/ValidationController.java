/*
 * Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.example.controller.exception;


import com.example.utils.Const;
import com.example.utils.Result;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ValidationController {
    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    public Result<String> validateException(ValidationException exception) {
        log.warn("Resolve [{} : {}]", exception.getClass().getName(), exception.getMessage());
        return Result.failure(Const.STATUS_CODE_400, "请求参数有误，请重新输入");
    }
}
