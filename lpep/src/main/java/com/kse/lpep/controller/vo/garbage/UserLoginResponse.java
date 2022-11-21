package com.kse.lpep.controller.vo.garbage;

import com.kse.lpep.controller.vo.BaseResponse;
import lombok.Data;
import lombok.experimental.Accessors;

//@Data
//@Accessors(chain = true)
//public class UserLoginResponse {
//    // 标识用户登录是否成功
//    private boolean success;
//
//    // 记录用户登录信息（账号不存在；账号密码错误；用户登录成功）
//    private String msg;
//
//    // 用户唯一标识，id
//    private String id;
//
//    private String userName;
//
//    private String realName;
//
//    // 用户是否为管理员
//    private Integer isAdmin;
//
//}

@Data
@Accessors(chain = true)
public class UserLoginResponse extends BaseResponse {
    private UserLoginData data;
}

