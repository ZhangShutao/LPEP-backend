package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NextPhaseStatusResult {
    // 0下阶段为非编程题；1下阶段为编程题
    private Integer isProg;
    // 0实验没有结束；1实验结束
    private Integer isEnd;
}
