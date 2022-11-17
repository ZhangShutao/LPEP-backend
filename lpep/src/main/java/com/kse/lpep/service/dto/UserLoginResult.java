package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户登录信息封装
 * 情况1：用户名不存在或者账号密码错误此类为null
 * 情况2：用户成功登录，返回正确的信息
 */
@Data
@Accessors(chain = true)
public class UserLoginResult {
    private String id;
    private String username;
    private String realname;
    private Integer isAdmin;
}
