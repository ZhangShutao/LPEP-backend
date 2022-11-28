package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CreateExperRequest {
    @NotBlank(message = "创建者id不能为空")
    private String creatorId;

    @NotBlank(message = "实验名不能为空")
    private String experName;

    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @NotBlank(message = "实验工作路径不能为空")
    private String workspace;

    List<CreateGroupInfo> groupInfoList;

    List<CreatePhaseInfo> phaseInfoList;
}
