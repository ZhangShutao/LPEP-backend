package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

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
    private String options;
    private String remark;
    private Integer type;
}
