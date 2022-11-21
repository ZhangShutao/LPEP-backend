package com.kse.lpep.controller.vo;

import lombok.Data;

import java.util.List;

@Data
public class CreateExperRequest {
    private String creatorId;
    private String experName;
    private String startDate;
    private String startTime;
    private String workspace;
    List<CreateGroupInfo> groupInfoList;
    List<CreatePhaseInfo> phaseInfoList;

}
