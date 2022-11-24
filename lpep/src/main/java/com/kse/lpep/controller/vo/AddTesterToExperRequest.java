package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddTesterToExperRequest {
    @NotBlank(message = "用户id不能为空")
    private String userId;

    @NotBlank(message = "添加的实验id不能为空")
    private String experId;

    @NotBlank(message = "添加的组别id不能为空")
    private String groupId;
}
