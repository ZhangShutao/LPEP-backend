package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RunnerInfo {
    private String runnerId;
    private String runnerName;
}
