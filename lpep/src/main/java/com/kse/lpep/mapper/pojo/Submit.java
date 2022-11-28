package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_submit")
@NoArgsConstructor
public class Submit implements Serializable {
    private static final long serialVersionUID = -7676075562871211813L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String userId;
    private String questionId;
    private String userAnswer;
    private Timestamp submitTime;


    public Submit(String userId, String questionId, String answer) {
        this.userId = userId;
        this.questionId = questionId;
        this.userAnswer = answer;
    }
}
