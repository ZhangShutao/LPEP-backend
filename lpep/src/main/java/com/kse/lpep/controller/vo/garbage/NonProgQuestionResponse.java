package com.kse.lpep.controller.vo.garbage;

import com.kse.lpep.service.dto.NonProgQuestionInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class NonProgQuestionResponse {
    private List<NonProgQuestionInfo> questionInfos;
    /*
    状态0表示前端传入数据出错
    状态1表示正常回应
     */
    private int state;
    private String msg;
    // 实验是否结束，true表示实验结束
    private boolean isEnd;
}
