package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateGroupInfo {
    @NotBlank(message = "组别名不能为空")
    private String groupName;
    //private String runnerName; // 暂时不用
}
