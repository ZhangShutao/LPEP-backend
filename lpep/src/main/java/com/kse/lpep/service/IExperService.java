package com.kse.lpep.service;

import com.kse.lpep.service.dto.QuestionInfo;

import java.util.List;

public interface IExperService {
    /**
     * 获取非编程题（需要提交编码代码的为编程题，其他的全部为非编程题）
     * 非编程类题目是一次性获取所有的题目并排序
     * @param userId 用户id
     * @param experId 实验id
     * @param phaseNumber 实验阶段
     * @return 所有的非编程题目
     */
    List<QuestionInfo> getNonProgQuestion(String userId, String experId, int phaseNumber);
}
