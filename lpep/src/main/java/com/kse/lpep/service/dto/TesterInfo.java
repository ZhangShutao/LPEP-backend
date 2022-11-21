package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TesterInfo extends UserLoginResult{
    private String userId;
    private String createTime;
}
