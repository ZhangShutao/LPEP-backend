package com.kse.lpep.controller.vo.garbage;

import com.kse.lpep.service.dto.ExperInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ExperToPartResponse {
    private List<ExperInfo> experInfos;
    private int state;
    private String msg;
}
