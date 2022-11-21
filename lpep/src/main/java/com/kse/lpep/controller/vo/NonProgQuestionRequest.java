package com.kse.lpep.controller.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class NonProgQuestionRequest {
    private String userId;
    private String experId;
    private int phaseNumber;
    //private Timestamp startTime;
}
