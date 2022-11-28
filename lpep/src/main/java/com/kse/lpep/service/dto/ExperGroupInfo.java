package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ExperGroupInfo {
    private String experId;
    private String experName;
    private List<GroupInfo> groupInfoList;
}
