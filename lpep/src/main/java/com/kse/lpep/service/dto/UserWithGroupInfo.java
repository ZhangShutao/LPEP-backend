package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserWithGroupInfo {
    private String userId;
    private String username;
    private String realname;
    private String group;
    private String groupName;
}
