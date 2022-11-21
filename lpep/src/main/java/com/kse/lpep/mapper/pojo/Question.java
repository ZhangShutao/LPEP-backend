package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_question")
public class Question {
    @TableId
    private String id;
    private String phaseId;
    private Integer number;
    private String content;
    private String options;
    private String answer;
    private Timestamp createTime;
    private String groupId;
    private String experId;
    private String remark;
    private Integer type;
}
