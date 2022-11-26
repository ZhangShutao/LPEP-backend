package com.kse.lpep.service;

import com.kse.lpep.mapper.pojo.UserFootprint;
import com.kse.lpep.service.dto.*;

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

    /**
     * 按照题号获取编程题
     * @param userId
     * @param experId
     * @param phaseNumber
     * @param questionNumber
     * @return
     */
    ProgQuestionResult acquireProgQuestion(String userId, String experId,
                                           int phaseNumber, int questionNumber);

    /**
     * 管理员获取所有的实验
     * @return
     */
    ExperInfoPage getAllExper(int pageIndex, int pageSize);

    /**
     * 修改实验状态，主要服务于管理员的实验开始和实验结束
     * @param experId
     */
    void modifyExperStatus(String experId, int currentStatus, int targetStatus);

    /**
     * 查询当前实验状态，主要是为了校验用
     * @param experId
     * @return
     */
    int queryExperCurrentStatus(String experId);

    /**
     * 列举所有求解器的类型
     * @return
     */
    List<RunnerInfo> listRunnerType();

    /**
     * 分页查询用户未参与的实验
     */
    ExperInfoPage queryNotInExpers(String userId, int pageIndex, int pageSize);

    /**
     * 根据实验id查询组别
     */
    List<GroupInfo> queryAllGroups(String experId);

    /*
    非编程题提交
     */
    String submitNonProg(String userId, String experId, int phaseNumber, List<UserAnswerDto> answers);

}
