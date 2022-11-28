package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CreateUserRequest {
    @NotBlank(message = "用户账号不能为空")
    private String username;

    @NotBlank(message = "用户真实姓名不能为空")
    private String realname;

    @NotNull(message = "用户管理员设置不能为空")
    @Max(value = 1,message = "用户管理员设置值不能大于1")
    @Min(value = 0,message = "用户管理员设置值不能小于0")
    private Integer isAdmin;
}
