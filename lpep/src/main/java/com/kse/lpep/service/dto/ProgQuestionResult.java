package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProgQuestionResult {
    private String questionId;
    // 记录题号
    private Integer number;
    private String content;
    private String remark;
    // 编程题限制用时，单位为分钟
    private Integer timeLimit;
    // 编程题runner限制用时，单位为秒
    private Integer runtimeLimit;
    // 是否为该阶段的最后一道题，1表示是（结束该阶段），0表示不是（后面还有题）
    private Integer isLast;
}
