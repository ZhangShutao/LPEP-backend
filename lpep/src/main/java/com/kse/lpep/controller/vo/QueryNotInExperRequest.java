package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class QueryNotInExperRequest {
    @NotBlank(message = "用户id不能为空")
    private String userId;

    @NotNull(message = "当前页码不能为空")
    @Min(value = 0, message = "当前页码最小值为0")
    private Integer pageIndex;

    @NotNull(message = "页面大小不能为空")
    @Min(value = 1, message = "每页大小最小为1")
    private Integer pageSize;
}
