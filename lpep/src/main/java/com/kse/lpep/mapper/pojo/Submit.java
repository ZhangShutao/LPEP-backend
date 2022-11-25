package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("t_submit")
public class Submit {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    private String userId;
    private String questionId;
    private String userAnswer;
    private Timestamp submitTime;
}
