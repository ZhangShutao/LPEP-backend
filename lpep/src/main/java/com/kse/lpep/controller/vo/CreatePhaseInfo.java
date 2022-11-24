package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreatePhaseInfo {
    @NotNull(message = "number不能为空")
    @Min(value = 1,message = "number最小值为1")
    private Integer number;

    @NotBlank(message = "阶段名不能为空")
    private String phaseName;

    @NotNull(message = "阶段类型不能为空")
    @Max(value = 1,message = "阶段类型值最大为1")
    @Min(value = 0,message = "阶段类型值最小为0")
    private Integer phaseType;
}
