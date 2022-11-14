package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户登录信息封装
 * service层处理后返回给controller层
 */
@Data
@Accessors(chain = true)
public class UserLoginResult {
    /*
     用户登录状态：
        0 表示用户名不存在
        1 表示用户名账号密码验证错误
        2 表示用户成功登录
     */
    private int state;
    private String id;
    private String userName;
    private String realName;
    private Integer isAdmin;
}
