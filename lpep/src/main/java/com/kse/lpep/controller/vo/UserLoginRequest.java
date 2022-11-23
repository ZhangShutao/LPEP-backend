package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserLoginRequest {
    @NotBlank(message = "用户账号不能为空")
    @Size(max=32, min=1)
    private String username;

    @NotBlank(message = "用户密码不能为空")
    private String password;
}
