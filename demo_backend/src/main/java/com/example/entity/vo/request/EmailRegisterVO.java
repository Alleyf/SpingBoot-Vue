/*
 * Copyright (c) alleyf 2023-11. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class EmailRegisterVO {
    @Email
    @Length(min = 6, max = 30)
    String email;
    @Length(min = 6, max = 6)
    String code;
    @Pattern(regexp = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$")
    @Length(min = 2, max = 15)
    String username;
    @Length(min = 6, max = 20)
    String password;
}
