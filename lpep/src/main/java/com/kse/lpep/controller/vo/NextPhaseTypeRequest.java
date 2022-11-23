package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class NextPhaseTypeRequest {
    @NotBlank(message = "用户id不能为空")
    private String userId;
    private String experId;
    private int phaseNumber;
}
