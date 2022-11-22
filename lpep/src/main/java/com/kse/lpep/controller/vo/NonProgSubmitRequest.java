package com.kse.lpep.controller.vo;

import com.kse.lpep.service.dto.UserAnswerDto;
import lombok.Data;

import java.util.List;

@Data
public class NonProgSubmitRequest {
    private String userId;
    private List<UserAnswerDto> answers;
}
