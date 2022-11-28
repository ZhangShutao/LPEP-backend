package com.kse.lpep.controller.vo;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.*;
import java.util.List;

@Data
public class AddNonProgQuestionInfo {

    @NotNull(message = "问题题号不能为空")
    @Min(value = 1,message = "问题题号最小值为1")
    private Integer number;

    // 问题类型：选择还是问答
    @NotNull(message = "问题类型不能为空")
    @Max(value = 1,message = "问题类型值最大为1")
    @Min(value = 0,message = "问题类型值最小为0")
    private Integer type;

    @NotBlank(message = "题目不能为空")
    private String content;

//    @Size(min = 1, max = 20, message = "options最少需要一个选项，最多只能有二十个选项")
    private List<String> options;

    // 非编程题中有问答
    private String answer;
}
