package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;


/**
 * 返回用户待参与的所有实验
 * 主要注意可能存在因意外关闭的实验
 */

@Data
@Accessors(chain = true)
public class ExperInfo {
    // 实验id
    private String id;
    // 实验名称
    private String title;
    // 实验开始时间
    private Timestamp startTime;
    /*
       状态0表示存在中断的实验，所有的其他正常实验状态全部置0
       状态1为正常状态
       状态2表示用户存在中断的实验
     */
    private Integer state;
    private String CurrentPhaseId;
    private String CurrentQuestionId;
    private Timestamp CurrentStartTime;
}
