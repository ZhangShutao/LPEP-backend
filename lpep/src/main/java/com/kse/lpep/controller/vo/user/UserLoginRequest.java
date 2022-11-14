package com.kse.lpep.controller.vo.user;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
}
