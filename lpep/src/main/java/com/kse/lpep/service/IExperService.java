package com.kse.lpep.service;

import com.kse.lpep.mapper.pojo.UserFootprint;
import com.kse.lpep.service.dto.NextPhaseStatusResult;
import com.kse.lpep.service.dto.NonProgQuestionInfo;
import com.kse.lpep.service.dto.ProgQuestionResult;

import java.util.List;

public interface IExperService {

    /**
     * 判断下阶段的问题类型，判断实验是否结束
     * @param experId
     * @param phaseNumber
     * @return
     */
    NextPhaseStatusResult acquirePhaseStatus(String userId, String experId, int phaseNumber);

    /**
     * 获取非编程题（需要提交编码代码的为编程题，其他的全部为非编程题）
     * 非编程类题目是一次性获取所有的题目并排序
     * 出现错误抛出NullPointer异常
     * @param userId 用户id
     * @param experId 实验id
     * @param phaseNumber 实验阶段
     * @return 所有的非编程题目
     */
    List<NonProgQuestionInfo> acquireNonProgQuestion(String userId, String experId, int phaseNumber);

    ProgQuestionResult acquireProgQuestion(String userId, String experId,
                                           int phaseNumber, int questionNumber);

}
