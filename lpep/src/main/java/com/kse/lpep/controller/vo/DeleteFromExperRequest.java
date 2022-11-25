package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeleteFromExperRequest {
    @NotBlank(message = "实验id不能为空")
    private String experId;

    @NotBlank(message = "用户id不能为空")
    private String userId;
}
