package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_prog_submit")
public class ProgSubmit implements Serializable {
    private static final long serialVersionUID = 5665552944925643191L;
    @TableId(type = IdType.ASSIGN_UUID)
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
