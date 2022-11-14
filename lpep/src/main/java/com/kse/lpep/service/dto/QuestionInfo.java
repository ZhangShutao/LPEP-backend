package com.kse.lpep.service.dto;

import lombok.Data;

/**
 * 封装非编程问题
 */
@Data
public class QuestionInfo {
    private String questionId;
    private String content;
    // 按照题号进行排序
    private Integer number;
    private String options;

}
