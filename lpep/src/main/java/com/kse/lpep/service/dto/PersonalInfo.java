package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class PersonalInfo {
    private String username;
    private String realname;
    private String createTime;
}
