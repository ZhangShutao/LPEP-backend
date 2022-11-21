package com.kse.lpep.controller.vo;

import lombok.Data;

@Data
public class AddNonProgQuestionInfo {
    private Integer number;
    // 问题类型：选择还是问答
    private Integer type;
    private String content;
    private String options;
    private String answer;
}
