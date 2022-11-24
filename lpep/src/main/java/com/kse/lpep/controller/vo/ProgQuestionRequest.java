package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ProgQuestionRequest {
    @NotBlank(message = "用户id不能为空")
    private String userId;

    @NotBlank(message = "实验id不能为空")
    private String experId;

    @NotNull(message = "阶段顺序不能为空")
    @Min(value = 1,message = "阶段顺序最小值为1")
    private Integer phaseNumber;

    @NotNull(message = "题号不能为空")
    private Integer questionNumber;
}
