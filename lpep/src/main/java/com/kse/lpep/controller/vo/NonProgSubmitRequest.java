package com.kse.lpep.controller.vo;

import com.kse.lpep.service.dto.UserAnswerDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class NonProgSubmitRequest {
    @NotBlank(message = "用户id不能为空")
    private String userId;

    private Integer phaseNumber;

    private String experId;

    private List<UserAnswerDto> answers;
}
