package com.kse.lpep.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张舒韬
 * @since 2022/11/23
 */
@Getter
@Setter
public class JudgeTask {
    public enum Type {
        ASP,
        CDLP
    }

    public enum State {
        PENDING, // 写入数据库
        RUNNING, // 进入推理机
        JUDGING, // 正在与标准结果比对
        ABORTED, // 取消测试
        ACCEPTED,
        WRONG,
        TIME_LIMIT_EXCEEDED,
        SYNTAX_ERROR
    }

    /**
     * 本次任务对应的数据库提交记录的id
     */
    private String progSubmitId;

    /**
     * 用户提交的代码
     */
    private String code;

    /**
     * 从数据库中获得的测试数据输入文件列表，按测试样例的序号排序
     */
    private List<String> inputFilePaths;

    /**
     * 根据用户提交的程序生成的输出
     */
    private List<String> output;

    private Type type;

    private State state;

    // TODO 添加 progSubmitId
    public JudgeTask(String code, Type type) {
        this.code = code;
        this.type = type;
        this.state = State.PENDING;
        this.inputFilePaths = new ArrayList<>();
        this.output = new ArrayList<>();
    }
}
