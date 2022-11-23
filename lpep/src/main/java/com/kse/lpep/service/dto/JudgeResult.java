package com.kse.lpep.service.dto;

/**
 * TODO 用户针对编程类问题的提交对应的测试结果
 * @author 张舒韬
 * @since 2022/11/21
 */
public class JudgeResult {
    public enum Status {
        ACCEPTED,
        WRONG_ANSWER,
        SYNTAX_ERROR,
        TIME_LIMIT_EXCEEDED,
        UNKNOWN_ERROR
    }

    /**
     * 提交执行结果
     */
    public Status status;

    /**
     * 程序执行和测试的错误信息：
     * 如果 status 为 ACCEPTED，则该字符串为空；
     * 如果 status 为 WRONG_ANSWER，则该字段包含首个错误样例的序号；
     * 如果 status 为 SYNTAX_ERROR，则该字段包含推理机的错误信息；
     * 如果 status 为 TIME_LIMIT_EXCEEDED，则该字段为 “超过限定求解时间”；
     * 如果 status 为 UNKNOWN_ERROR，则该字段为错误异常的 message；
     */
    public String errorMsg;

    /**
     * 首个错误样例的输入数据
     */
    public String wrongCaseInput;

    /**
     * 首个错误样例的标准输出
     */
    public String standardOutput;

    /**
     * 首个错误样例在用户提交的程序下运行的结果
     */
    public String userOutput;
}
