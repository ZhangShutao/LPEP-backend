package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("t_prog_question")
public class ProgQuestion {
    private String id;
    private String phaseId;
    private Integer number;
    private String content;   //映射表中的text字段
    private String testInputPath;
    private String testOutputPath;
    private Timestamp createTime;
    private String groupId;
    private String experId;
    private String remark;
    private String runnerId;
    private Integer timeLimit;
    private Integer runtimeLimit;
}
