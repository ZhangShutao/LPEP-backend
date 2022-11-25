package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@TableName("t_user_group")
public class UserGroup {
    private String id;
    private String userId;
    private String groupId;
    private String experId;

    public UserGroup(String userId, String groupId, String experId) {
        this.userId = userId;
        this.groupId = groupId;
        this.experId = experId;
    }

}
