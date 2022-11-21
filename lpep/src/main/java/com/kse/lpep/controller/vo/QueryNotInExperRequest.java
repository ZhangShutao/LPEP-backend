package com.kse.lpep.controller.vo;

import lombok.Data;

@Data
public class QueryNotInExperRequest {
    private String userId;
    private Integer pageIndex;
    private Integer pageSize;
}
