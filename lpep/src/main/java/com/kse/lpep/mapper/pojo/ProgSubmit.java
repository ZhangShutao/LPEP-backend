package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("t_prog_submit")
public class ProgSubmit {
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
