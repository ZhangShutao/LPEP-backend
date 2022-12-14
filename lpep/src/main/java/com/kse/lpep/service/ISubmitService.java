package com.kse.lpep.service;

import com.kse.lpep.common.exception.NoSuchRecordException;
import com.kse.lpep.common.exception.NotAuthorizedException;
import com.kse.lpep.service.dto.JudgeTask;
import com.kse.lpep.service.dto.ProgramSubmitInfo;

import java.io.IOException;
import java.util.List;

/**
 * @author 张舒韬
 * @since 2022/11/24
 */
public interface ISubmitService {
    /**
     * 写入编程题提交记录并生成测试任务
     * @param userId 提交用户id
     * @param problemId 提交问题id
     * @param code 提交代码
     * @return 生成的测试任务对象
     * @throws NoSuchRecordException 没有对应的用户或编程题
     * @throws NotAuthorizedException 用户当前不可以提交该问题
     */
    List<JudgeTask> submitProgram(String userId, String problemId, String code)
            throws NoSuchRecordException, NotAuthorizedException, IOException;

    /**
     * 更新编程题提交的阶段
     * @param submitId 提交记录的id
     * @param status 提交的状态
     * @return 状态更新成功
     * @throws NoSuchRecordException 没有指定的提交记录
     */
    Boolean updateProgramSubmitState(String submitId, JudgeTask.Status status)
            throws NoSuchRecordException;

    /**
     * 用户放弃对当前问题的求解
     * @param userId 提交用户id
     * @param problemId 被放弃的问题的id
     * @return 如果成功删除，则返回true；否则返回false
     * @throws NoSuchRecordException 没有对应的用户或编程题
     * @throws NotAuthorizedException 该用户当前不可以放弃该问题
     */
    Boolean abortProgram(String userId, String problemId)
            throws NoSuchRecordException, NotAuthorizedException;

    /**
     * 写入用户对目标问卷阶段提交的答题结果
     * @param userId 提交用户id
     * @param phaseId 目标阶段id
     * @param answer 用户对该问卷阶段的回答，为一个json列表，其中每项包含 problemId 和 userAnswer 两个字段。
     * @return 提交成功则返回true，否则返回false
     * @throws NoSuchRecordException 没有对应的用户或问答阶段，或者用户的回答对应的问题不存在；
     * @throws NotAuthorizedException 该用户没有对目标阶段提交的权限
     */
    Boolean submitQuestionnaire(String userId, String phaseId, String answer)
            throws NoSuchRecordException, NotAuthorizedException;


    /**
     * 用户提交编程题（成功或放弃）后修改用户的状态
     * @param userId
     * @param questionId
     */
    void modifyProgUserFootprint(String userId, String questionId);


    /**
     * 校验用户的编程题重复提交问题，重复提交则抛出异常
     * @param userId
     * @param progQuestionId
     */
    void validRepeat(String userId, String progQuestionId);

    /**
     * 分页查询用户提交信息，按时间降序排列
     * @param userId 用户id
     * @param questionId 问题id
     * @param pageIndex 页码
     * @param pageSize 页大小
     * @return 查询结果
     * @see ProgramSubmitInfo
     */
    List<ProgramSubmitInfo> listProgramSubmitInfo(String userId, String questionId, int pageIndex,int pageSize) throws NotAuthorizedException, NoSuchRecordException;

    /**
     * 获取分页查询的总记录数
     * @param userId
     * @param questionId
     * @return
     */
    int getRecordNumber(String userId, String questionId);
}
