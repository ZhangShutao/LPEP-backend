package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddProgQuestionRequest {
    @NotBlank(message = "实验id不能为空")
    private String experId;

    @NotBlank(message = "组别id不能为空")
    private String groupId;

    @NotNull(message = "阶段顺序不能为空")
    private Integer phaseNumber;

    List<AddProgQuestionInfo> addProgQuestionInfoList;


//    private Integer questionNumber;
//    private String content;
//    private Integer timeLimit;
//    private String runnerId;
//    private Integer runtimeLimit;
//    List<String> caseIds;
}
