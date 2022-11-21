package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_prog_submit")
public class ProgSubmit {
    @TableId
    private String id;
    private String userId;
    private String questionId;
    private String runnerOutput;
    private Integer status;
    private String sourceCode;
    private Timestamp submitTime;
    private Integer usedTime;
    private Double runnerTime;
}
