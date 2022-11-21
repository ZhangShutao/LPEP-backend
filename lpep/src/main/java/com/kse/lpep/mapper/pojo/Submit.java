package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_submit")
public class Submit {
    @TableId
    private String id;
    private String userId;
    private String questionId;
    private String userAnswer;
    private Timestamp submitTime;
}
