package com.kse.lpep.controller.vo;

import lombok.Data;

@Data
public class AddTesterToExperRequest {
    private String userId;
    private String experId;
    private String groupId;
}
