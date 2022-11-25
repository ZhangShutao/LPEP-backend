package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

@Data
@TableName("t_prog_submit")
public class ProgSubmit {


    // 状态信息

    /**
     * 未进入测试
     */
    public final static Integer NOT_TESTED = 0;

    /**
     * 在测试队列中
     */
    public final static Integer TESTING = 1;

    /**
     * 测试通过
     */
    public final static Integer ACCEPTED = 2;

    /**
     * 测试未通过，对某组测试数据的运行结果错误
     */
    public final static Integer WRONG_ANSWER = 3;

    /**
     * 测试未通过，提交代码存在语法错误
     */
    public final static Integer SYNTAX_ERROR = 4;

    /**
     * 测试未通过，对某组测试数据的运行时间超过限制
     */
    public final static Integer TIME_LIMIT_EXCEEDED = 5;

    /**
     * 未知类型错误，没有成功运行测试
     */
    public final static Integer UNKNOWN_ERROR = 6;


    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @NotNull
    private String userId;

    @NotNull
    private String questionId;
    private String runnerOutput;
    private Integer status;
    private String sourceCode;
    private Timestamp submitTime;
    private Integer usedTime;
    private Double runnerTime;

    public ProgSubmit(@NotNull String userId, @NotNull String questionId, Integer status, String sourceCode) {
        this.userId = userId;
        this.questionId = questionId;
        this.status = status;
        this.sourceCode = sourceCode;
    }
}
