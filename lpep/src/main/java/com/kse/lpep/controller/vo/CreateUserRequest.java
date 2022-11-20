package com.kse.lpep.controller.vo;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String realname;
    private Integer isAdmin;
}
