package com.kse.lpep.controller.vo;

import lombok.Data;

/**
 * 用户对程序类问题的提交请求
 * @author 张舒韬
 * @since 2022/11/21
 */
@Data
public class ProgramSubmitRequest {

    /**
     * 提交用户的id
     */
    private String userId;

    /**
     * 本次提交对应的问题的id
     */
    private String problemId;

    /**
     * 用户提交的代码
     */
    private String source;

}
