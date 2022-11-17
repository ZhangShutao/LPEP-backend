package com.kse.lpep.controller.vo.garbage;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginData{
    // 用户唯一标识，id
    private String id;
    private String userName;
    private String realName;
    // 用户是否为管理员
    private Integer isAdmin;
}
