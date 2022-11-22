package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 封装非编程问题
 */
@Data
@Accessors(chain = true)
public class NonProgQuestionInfo {
    private String questionId;
    private String content;
    // 按照题号进行排序
    private Integer number;
    private List<String> options;
    private String remark;
    // 选择还是问题
    private Integer type;
}
