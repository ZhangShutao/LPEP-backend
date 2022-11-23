package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_submit")
public class Submit implements Serializable {
    private static final long serialVersionUID = -7676075562871211813L;
    @TableId
    private String id;
    private String userId;
    private String questionId;
    private String userAnswer;
    private Timestamp submitTime;
}
