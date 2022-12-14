package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户对问卷类型阶段发起的提交请求内容
 * @author 张舒韬
 * @since 2022/11/21
 */
@Data
public class QuestionnaireSubmitRequest {

    /**
     * 发起提交的用户id
     */
    @NotBlank
    private String userId;

    /**
     * 问卷阶段的阶段id
     */
    @NotBlank
    private String phaseId;

    /**
     * 用户提交的问卷，是一个json字符串
     */
    @NotBlank
    private String answer;
}
