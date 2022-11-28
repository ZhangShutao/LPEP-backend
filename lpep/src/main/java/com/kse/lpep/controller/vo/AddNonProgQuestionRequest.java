package com.kse.lpep.controller.vo;

import com.kse.lpep.service.dto.NonProgQuestionInfo;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddNonProgQuestionRequest {
    @NotBlank(message = "实验id不能为空")
    private String experId;

    @NotBlank(message = "组别id不能为空")
    private String groupId;

    @NotNull(message = "阶段顺序不能为空")
    @Min(value = 1, message = "阶段顺序最小值为1")
    @Max(value = 20, message = "阶段顺序最大值为10")
    private Integer phaseNumber;

    List<AddNonProgQuestionInfo> addNonProgQuestionInfoList;
}
