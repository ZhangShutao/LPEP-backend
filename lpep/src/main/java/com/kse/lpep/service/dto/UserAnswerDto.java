package com.kse.lpep.service.dto;

import lombok.Data;

@Data
public class UserAnswerDto {
    // 非编程题id
    private String questionId;
    // 用户作答答案
    private String reply;
}
