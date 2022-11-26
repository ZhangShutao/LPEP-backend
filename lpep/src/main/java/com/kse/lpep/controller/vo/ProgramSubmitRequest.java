package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

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
    @NotBlank(message = "用户id不能为空")
    private String userId;

    /**
     * 本次提交对应的问题的id
     */
    @NotBlank(message = "问题id不能为空")
    private String problemId;

    /**
     * 用户提交的代码
     */
    @NotBlank(message = "用户提交的源代码不能为空")
    private String source;

}
