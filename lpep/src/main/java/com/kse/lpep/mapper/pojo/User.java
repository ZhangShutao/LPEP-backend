package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_user")
public class User {
    private String id;
    private String username;
    private String password;
    private String realname;
    private Integer isAdmin;
    private Timestamp createTime;
}
