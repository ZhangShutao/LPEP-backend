package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_user")
public class User {
    @TableId
    private String id;
    private String username;
    private String password;
    private String realname;
    private Integer isAdmin;
//    @TableField(fill = FieldFill.INSERT)
    private Timestamp createTime;
}
