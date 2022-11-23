package com.kse.lpep.controller.vo;

import lombok.Data;

import java.util.List;

@Data
public class AddProgQuestionRequest {
    private String experId;
    private String groupName;
    private Integer phaseNumber;
    private Integer questionNumber;
    private String content;
    private Integer timeLimit;
    private String runnerId;
    private Integer runtimeLimit;
    List<String> caseIds;
}