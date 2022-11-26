package com.kse.lpep.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserAnswerDto {
    // 非编程题id
    @NotBlank(message = "问题id不能为空")
    private String questionId;
    // 用户作答答案
    @NotBlank(message = "用户答案不能为空")
    private String reply;
}
