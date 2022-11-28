package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserWithGroupInfoPage {
    private Integer recordCount;
    private List<UserWithGroupInfo> userWithGroupInfoList;
}
