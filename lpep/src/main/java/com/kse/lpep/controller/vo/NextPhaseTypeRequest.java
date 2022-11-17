package com.kse.lpep.controller.vo;

import lombok.Data;


@Data
public class NextPhaseTypeRequest {
    private String userId;
    private String experId;
    private int phaseNumber;
}
