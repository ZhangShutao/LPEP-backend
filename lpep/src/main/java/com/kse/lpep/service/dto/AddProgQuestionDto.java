package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddProgQuestionDto {
    private String experId;
    private String groupName;
    private Integer phaseNumber;
    private Integer questionNumber;
    private String content;
    private Integer timeLimit;
    private String runnerId;
    private Integer runtimeLimit;
}
