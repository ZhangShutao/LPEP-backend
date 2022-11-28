package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GroupInfo {
    private String groupId;
    private String groupName;
}
