package com.kse.lpep.service.dto;

import lombok.Getter;
import lombok.Setter;

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

    public enum Status {
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
     * 测试样例序号
     */
    private Integer caseNumber;

    /**
     * 用户提交的代码
     */
    private String sourcePath;

    /**
     * 测试数据的输入文件路径
     */
    private String inputPath;

    /**
     * 保存测试数据的标准输出的文件路径
     */
    private String standardOutputPath;

    /**
     * 根据用户提交的程序生成的输出，包括语法错误提示
     */
    private String output;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 运行命令
     */
    private String cmd;

    /**
     * 运行时间上限，单位为秒
     */
    private Integer timeLimit;

    private Status status;

    public JudgeTask(String progSubmitId, Integer caseNumber,
                     String sourcePath, String inputPath, String standardOutputPath, String output,
                     String cmd, Integer timeLimit) {
        this.progSubmitId = progSubmitId;
        this.sourcePath = sourcePath;
        this.inputPath = inputPath;
        this.standardOutputPath = standardOutputPath;
        this.output = output;
        this.cmd = cmd;
        this.timeLimit = timeLimit;
    }
}
