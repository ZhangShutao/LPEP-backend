package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_user_footprint")
public class UserFootprint implements Serializable {
    private static final long serialVersionUID = 4250463149954568608L;
    @TableId
    private String id;
    private String userId;
    private String experId;
    private String groupId;
    private Integer currentPhaseNumber;
    private Integer currentQuestionNumber;
    private Timestamp startTime;
    private Integer isEnd;
    private Timestamp completeTime;
}
