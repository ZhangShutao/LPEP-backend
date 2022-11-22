package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_prog_question")
public class ProgQuestion {
    @TableId(type = IdType.ASSIGN_UUID)
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
