package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_user")
public class User implements Serializable {
    private static final long serialVersionUID = -3612736939018296764L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String username;
    private String password;
    private String realname;
    private Integer isAdmin;
    private Timestamp createTime;
}
