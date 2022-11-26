package com.kse.lpep.service.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO 用户针对编程类问题的提交对应的测试结果
 * @author 张舒韬
 * @since 2022/11/21
 */
@Getter
@Setter
public class JudgeResult {
    public enum Status {
        ACCEPTED("accept"),
        WRONG_ANSWER("wrong answer"),
        SYNTAX_ERROR("syntax error"),
        TIME_LIMIT_EXCEEDED("time limit exceeded"),
        UNKNOWN_ERROR("unknown error");

        private String code;

        private Status(String code) {
            this.code = code;
        }
        public String getCode() {
            return this.code;
        }
    }

    /**
     * 提交执行结果
     */
    private Status status;

    /**
     * 程序执行和测试的错误信息：
     * 如果 status 为 ACCEPTED，则该字符串为空；
     * 如果 status 为 WRONG_ANSWER，则该字段包含首个错误样例的序号；
     * 如果 status 为 SYNTAX_ERROR，则该字段包含推理机的错误信息；
     * 如果 status 为 TIME_LIMIT_EXCEEDED，则该字段为首个超时样例的序号
     * 如果 status 为 UNKNOWN_ERROR，则该字段为错误异常的 message；
     */
    private String errorMsg;

    /**
     * 错误或超时数据的序号
     */
    private Integer numberOfWrong;

    /**
     * 首个错误样例或超时样例的输入数据
     */
    private String wrongCaseInput;

    /**
     * 首个错误样例的标准输出
     */
    private String standardOutput;

    /**
     * 首个错误样例在用户提交的程序下运行的结果
     */
    private String userOutput;
}
