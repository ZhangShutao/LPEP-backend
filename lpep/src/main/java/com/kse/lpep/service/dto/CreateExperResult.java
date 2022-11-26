package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CreateExperResult {
    private String experId;
    private String experName;
    private String startTime;
    private Integer status;
    private List<GroupInfo> groups;
}
