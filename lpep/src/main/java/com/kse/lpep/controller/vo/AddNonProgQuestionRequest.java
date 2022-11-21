package com.kse.lpep.controller.vo;

import com.kse.lpep.service.dto.NonProgQuestionInfo;
import lombok.Data;

import java.util.List;

@Data
public class AddNonProgQuestionRequest {
    private String experId;
    private String groupName;
    private String phaseNumber;
    List<AddNonProgQuestionInfo> addNonProgQuestionInfoList;
}
