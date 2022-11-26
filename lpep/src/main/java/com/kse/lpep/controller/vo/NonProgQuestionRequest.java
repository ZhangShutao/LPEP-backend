package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class NonProgQuestionRequest {
    @NotBlank(message = "用户id不能为空")
    private String userId;

    @NotBlank(message = "实验id不能为空")
    private String experId;

    @NotNull(message = "实验阶段不能为空")
    @Min(value = 1,message = "阶段顺序最小值为1")
    private int phaseNumber;
    //private Timestamp startTime;
}
