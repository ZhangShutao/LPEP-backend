package com.kse.lpep.controller.vo;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddNonProgQuestionInfo {

    private Integer number;

    // 问题类型：选择还是问答
    @NotNull
    @Max(1)
    @Min(0)
    private Integer type;

    @NotBlank
    private String content;
    private List<String> options;
    @NotBlank
    private String answer;
}
