package com.kse.lpep.service;

import com.kse.lpep.common.exception.NoSuchRecordException;
import com.kse.lpep.common.exception.NotAuthorizedException;
import com.kse.lpep.service.dto.JudgeTask;

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
    JudgeTask submitProgram(String userId, String problemId, String code)
            throws NoSuchRecordException, NotAuthorizedException;

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
}
