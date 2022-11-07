package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("t_question")
public class Question {
    private String id;
    private String content;
    private String options;
    private String answer;
    private Timestamp createTime;
    private String groupId;
    private String experId;
    private String remark;
    private Integer type;
}
