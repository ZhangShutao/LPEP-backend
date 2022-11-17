package com.kse.lpep.controller.vo.garbage;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class QuestionTypeResponse {
    // type为0表示非编程，1表示编程
    private Integer type;
    private String msg;
    // 状态0表示异常（此时type值失效），状态1表示正常
    private Integer state;
}
