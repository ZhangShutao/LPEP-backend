package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
public class TesterInfo {
    private String userName;
    private String realName;
    private Timestamp createTime;
    // 查询失败，状态置0，表明该用户不存在（说明前端出问题了）
    private Integer state;
    private String msg = "该用户不存在";
}
