package com.kse.lpep.service.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 张舒韬
 * @since 2022/11/26
 */
@Data
public class ProgramSubmitInfoPage {

    private Integer recordCount;

    private List<ProgramSubmitInfo> programSubmitInfoList;

    public ProgramSubmitInfoPage(List<ProgramSubmitInfo> list, int recordCount) {
        this.recordCount = recordCount;
        this.programSubmitInfoList = list;
    }
}
