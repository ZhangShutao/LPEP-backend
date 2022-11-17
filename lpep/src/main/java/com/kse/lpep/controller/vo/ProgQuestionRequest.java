package com.kse.lpep.controller.vo;

import lombok.Data;

@Data
public class ProgQuestionRequest {
    private String userId;
    private String experId;
    private int phaseNumber;
    private int questionNumber;
}
