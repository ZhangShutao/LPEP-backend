package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("t_submit")
public class Submit {
    private String id;
    private String userId;
    private String questionId;
    private String userAnswer;
    private Timestamp submitTime;
}
