package com.kse.lpep.controller.vo;

import lombok.Data;

@Data
public class CreateExperRequest {
    private String experTitle;
    private String userId;
    private String startTime;
    private String confPath;
}
