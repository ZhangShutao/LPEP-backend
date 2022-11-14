package com.kse.lpep.controller.vo.exper;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ExperStartRequest {
    // 开始实验的用户id
    private String userId;
    private String experId;
    private String groupId;
    private Timestamp startTime;
}
