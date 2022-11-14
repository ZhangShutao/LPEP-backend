package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("t_user_footprint")
public class UserFootprint {
    private String id;
    private String userId;
    private String experId;
    private String groupId;
    private String currentPhaseId;
    private String currentQuestionId;
    private Timestamp startTime;
    private Integer isComplete;
    private Timestamp completeTime;
}
