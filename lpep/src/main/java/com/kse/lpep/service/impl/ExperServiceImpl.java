package com.kse.lpep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kse.lpep.mapper.*;
import com.kse.lpep.mapper.pojo.*;
import com.kse.lpep.service.IExperService;
import com.kse.lpep.service.dto.NextPhaseStatusResult;
import com.kse.lpep.service.dto.NonProgQuestionInfo;
import com.kse.lpep.service.dto.ProgQuestionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperServiceImpl implements IExperService {
    @Autowired
    private IUserGroupMapper userGroupMapper;
    @Autowired
    private IPhaseMapper phaseMapper;
    @Autowired
    private IQuestionMapper questionMapper;
    @Autowired
    private IUserFootprintMapper userFootprintMapper;
    @Autowired
    private IProgQuestionMapper progQuestionMapper;

    /*
   1. 首先判断实验是否结束，结束则修改用户做题状态
   2. 如果实验没有结束，判断下一阶段题目类型
    */
    @Override
    public NextPhaseStatusResult acquirePhaseStatus(String userId, String experId, int phaseNumber) {
        // 查询表t_phase
        NextPhaseStatusResult nextPhaseStatusResult = new NextPhaseStatusResult();
        QueryWrapper<Phase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exper_id", experId).eq("phase_number", phaseNumber);
        QueryWrapper<Phase> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("exper_id", experId).eq("phase_number", phaseNumber - 1);
        Phase phase = phaseMapper.selectOne(queryWrapper);
        Phase phase1 = phaseMapper.selectOne(queryWrapper1);
        if(phase == null){
            // 不存在下一个阶段，实验结束
            if(phase1 != null){
                nextPhaseStatusResult.setIsEnd(1);
                // 实验结束则修改用户状态
                modifyUserFootprint(userId, experId,
                        phaseNumber - 1, null, 1);
            }else{
                // 前端数据传入错误
                throw new NullPointerException();
            }
        }else{
            nextPhaseStatusResult.setIsEnd(0).setIsProg(phase.getType());
        }
        return nextPhaseStatusResult;
    }


    /*
    前提条件：已经确定该实验没有结束
    1. 获取该实验的groupId和phaseId并判空，判断问题类型是否为非编程题
    2. 根据步骤1和步骤2中的数据查询表t_question获取问题列表
    3. 排序并封装该阶段所有的非编程问题
    4. 更改用户状态
     */
    @Override
    public List<NonProgQuestionInfo> acquireNonProgQuestion(String userId,  String experId, int phaseNumber) {
        // 1.获取groupId和phaseId并判空和题目类型
        QueryWrapper<UserGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("exper_id", experId);
        String groupId = userGroupMapper.selectOne(queryWrapper).getGroupId();
        QueryWrapper<Phase> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("exper_id", experId).eq("phase_number", phaseNumber);
        Phase phase = phaseMapper.selectOne(queryWrapper1);
        if(groupId == null || phase.getId() == null || phase.getType() == 1){
            throw new NullPointerException();
        }
        // 2.获取问题列表
        QueryWrapper<Question> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("group_id", groupId).eq("phase_Id", phase.getId());
        List<Question> questions = questionMapper.selectList(queryWrapper2);
        // 3.排序封装
        Collections.sort(questions, (a, b) -> a.getNumber() - b.getNumber());
        List<NonProgQuestionInfo> questionInfos = questions.stream()
                .map(question ->
                {
                    NonProgQuestionInfo questionInfo = new NonProgQuestionInfo();
                    questionInfo.setQuestionId(question.getId()).setContent(question.getContent())
                            .setNumber(question.getNumber()).setOptions(question.getOptions())
                            .setType(question.getType()).setRemark(question.getRemark());
                    return questionInfo;
                }).collect(Collectors.toList());
        // 4.更改用户状态
        if(phaseNumber == 1){
            // 创建一个对象
            UserFootprint userFootprint = new UserFootprint();
            userFootprint.setUserId(userId).setExperId(experId).setGroupId(groupId).setCurrentPhaseNumber(1);
            userFootprintMapper.insert(userFootprint);
        }else{
            // 修改状态，其中非编程题当前题号为null，实验不结束
            modifyUserFootprint(userId, experId, phaseNumber, null, 0);
        }
        return questionInfos;
    }

    /*
    前提条件：已经确定该实验没有结束
    1. 获取该实验的groupId和phaseId，结合phaseNumber查表判空，判断问题类型是否为非编程题
    2. 获取问题，并判断该问题是否为最后一题
    3. 更改用户状态
     */
    @Override
    public ProgQuestionResult acquireProgQuestion(String userId, String experId,
                                                  int phaseNumber, int questionNumber) {
        // 1.查表判空
        try {
            QueryWrapper<UserGroup> queryWrapper0 = new QueryWrapper<>();
            queryWrapper0.eq("user_id", userId).eq("exper_id", experId);
            String groupId = userGroupMapper.selectOne(queryWrapper0).getGroupId();
            QueryWrapper<Phase> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("exper_id", experId).eq("phase_number", phaseNumber);
            Phase phase = phaseMapper.selectOne(queryWrapper);
            QueryWrapper<ProgQuestion> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("group_id", groupId).eq("phase_id", phase.getId())
                    .eq("number", questionNumber);
            QueryWrapper<ProgQuestion> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("group_id", groupId).eq("phase_id", phase.getId())
                    .eq("number", questionNumber + 1);
            // 2.获取问题列表并封装
            ProgQuestion progQuestion = progQuestionMapper.selectOne(queryWrapper1);
            ProgQuestionResult progQuestionResult = new ProgQuestionResult();
            if(progQuestionMapper.selectOne(queryWrapper2) == null){
                progQuestionResult.setIsLast(1);
            }
            progQuestionResult.setQuestionId(progQuestion.getId()).setNumber(progQuestion.getNumber())
                    .setContent(progQuestion.getContent()).setRemark(progQuestion.getRemark())
                    .setRuntimeLimit(progQuestion.getRuntimeLimit()).setTimeLimit(progQuestion.getTimeLimit());
            // 3.修改用户状态
            if(phaseNumber == 1 && questionNumber == 1){
                UserFootprint userFootprint = new UserFootprint();
                // 这里没有插入组别
                userFootprint.setUserId(userId).setExperId(experId).setCurrentPhaseNumber(1)
                        .setCurrentQuestionNumber(1);
                userFootprintMapper.insert(userFootprint);
            }
            modifyUserFootprint(userId, experId, phaseNumber, questionNumber, 0);
            return progQuestionResult;
        }catch (NullPointerException e){
            // 如果出现空，说明前端数据传输错误或者非编程题
            throw new NullPointerException();
        }
    }

    private void modifyUserFootprint(String userId, String experId, Integer currentPhaseNumber,
                                     Integer currentQuestionNumber, int isEnd) {
        UpdateWrapper<UserFootprint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).eq("exper_id", experId);
        UserFootprint userFootprint = new UserFootprint();
        userFootprint.setCurrentPhaseNumber(currentPhaseNumber).setCurrentQuestionNumber(currentQuestionNumber)
                .setIsEnd(isEnd);
        userFootprintMapper.update(userFootprint, updateWrapper);
    }
}
