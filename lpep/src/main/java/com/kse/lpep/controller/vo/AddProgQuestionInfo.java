package com.kse.lpep.controller.vo;

import lombok.Data;

import java.util.List;

@Data
public class AddProgQuestionInfo {
    private Integer number;
    private String content;
    private Integer timeLimit;
    private String runnerName;
    List<TestDataInfo> testDataInfoList;

}
