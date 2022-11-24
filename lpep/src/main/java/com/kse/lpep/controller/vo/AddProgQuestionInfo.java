package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddProgQuestionInfo {
    @NotNull(message = "问题题号不能为空")
    @Min(value = 1,message = "问题题号最小值为1")
    private Integer questionNumber;

    @NotBlank(message = "题目不能为空")
    private String content;

    @NotNull(message = "问题限时不能为空")
    private Integer timeLimit;

    @NotBlank(message = "runnerId不能为空")
    private String runnerId;

    @NotNull(message = "runner运行限时不能为空")
    private Integer runtimeLimit;

    List<String> caseIds;
}
