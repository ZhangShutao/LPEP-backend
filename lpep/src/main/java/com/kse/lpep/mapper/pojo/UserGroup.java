package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("t_user_group")
public class UserGroup implements Serializable {
    private static final long serialVersionUID = 8496939899638839210L;
    @TableId
    private String id;
    private String userId;
    private String groupId;
    private String experId;
}
