package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class TesterInfoPage {
    private Integer recordCount;
    private List<TesterInfo> testerInfoList;
}
