package com.kse.lpep.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kse.lpep.common.exception.ElementDuplicateException;
import com.kse.lpep.mapper.*;
import com.kse.lpep.mapper.pojo.*;
import com.kse.lpep.service.IExperService;
import com.kse.lpep.service.dto.*;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
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
    @Autowired
    private IExperMapper experMapper;
    @Autowired
    private IRunnerMapper runnerMapper;
    @Autowired
    private IGroupMapper groupMapper;
    @Autowired
    private ISubmitMapper submitMapper;

    /*
   1. 首先判断实验是否结束，结束则修改用户做题状态
   2. 如果实验没有结束，判断下一阶段题目类型
    */
    @Transactional
    @Override
    public NextPhaseStatusResult acquirePhaseStatus(String userId, String experId, int phaseNumber) {
        // 查询表t_phase
        NextPhaseStatusResult nextPhaseStatusResult = new NextPhaseStatusResult();
        Phase phase = phaseMapper.selectByExperAndNumber(experId, phaseNumber);
        Phase phase1 = phaseMapper.selectByExperAndNumber(experId, phaseNumber - 1);
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
    @Transactional
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

                    List<String> options = JSON.parseArray(question.getOptions(), String.class);
                    questionInfo.setQuestionId(question.getId()).setContent(question.getContent())
                            .setNumber(question.getNumber()).setOptions(options)
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
    @Transactional
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

    @Override
    public ExperInfoPage getAllExper(int pageIndex, int pageSize) {
        Page<Exper> experPage = new Page<>(pageIndex, pageSize, true);
        IPage<Exper> experIPage = experMapper.selectPage(experPage, null);
        List<ExperInfo> experInfoList = experIPage.getRecords().stream()
                .map(exper ->
                {
                    ExperInfo experInfo = new ExperInfo();
                    String startTime = new SimpleDateFormat("yyyy-MM-dd").format(exper.getStartTime());
                    experInfo.setExperId(exper.getId()).setTitle(exper.getTitle()).setState(exper.getState())
                            .setStartTime(startTime);
                    return experInfo;
                }).collect(Collectors.toList());
        ExperInfoPage experInfoPage = new ExperInfoPage();
        experInfoPage.setRecordCount((int)experIPage.getTotal()).setExperInfoList(experInfoList);
        return experInfoPage;
    }

    @Transactional
    @Override
    public void modifyExperStatus(String experId, int currentStatus, int targetStatus) {
        // 首先查询实验是否存在
        // 实验状态只能从0变成2，2变成1，不存在其他状态
        Exper exper = experMapper.selectById(experId);
        // 实验不存在，抛出异常
        if(exper == null){
            throw new NullPointerException();
        }
        exper.setState(targetStatus);
        // 管理员点击实验开始
        if((currentStatus == 0 && targetStatus == 2) || (currentStatus == 2 && targetStatus == 1)){
            experMapper.updateById(exper);
            return;
        }
        throw new NullPointerException();
    }

    @Override
    public int queryExperCurrentStatus(String experId) {
        try{
            return experMapper.selectById(experId).getState();
        }catch (NullPointerException e){
            throw new NullPointerException();
        }
    }

    @Override
    public List<RunnerInfo> listRunnerType() {
        List<Runner> runnerList = runnerMapper.selectList(null);
        List<RunnerInfo> runnerInfoList = runnerList.stream()
                .map(r->{
                    RunnerInfo runnerInfo = new RunnerInfo();
                    runnerInfo.setRunnerId(r.getId()).setRunnerName(r.getName());
                    return runnerInfo;
                }).collect(Collectors.toList());
        return runnerInfoList;
    }

    // 管理员分页查询用户未参与的实验
    @Override
    public ExperInfoPage queryNotInExpers(String userId, int pageIndex, int pageSize) {
        QueryWrapper<UserGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<String> experIds = userGroupMapper.selectList(queryWrapper).stream()
                .map(UserGroup::getExperId).collect(Collectors.toList());

        LambdaQueryWrapper<Exper> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(experIds.size() != 0) {
            lambdaQueryWrapper.and(wp -> wp.eq(Exper::getState, 0).notIn(Exper::getId, experIds))
                    .or(wp -> wp.eq(Exper::getState, 2).notIn(Exper::getId, experIds));
        }else{
            lambdaQueryWrapper.eq(Exper::getState, 0).or().eq(Exper::getState, 2);
        }
        Page<Exper> experPage = new Page<>(pageIndex, pageSize, true);
        IPage<Exper> experIPage = experMapper.selectPage(experPage, lambdaQueryWrapper);
        List<ExperInfo> experInfoList = experIPage.getRecords().stream()
                .map(exper ->
                {
                    ExperInfo experInfo = new ExperInfo();
                    String startTime = new SimpleDateFormat("yyyy-MM-dd").format(exper.getStartTime());
                    experInfo.setExperId(exper.getId()).setTitle(exper.getTitle()).setState(exper.getState())
                            .setStartTime(startTime);
                    return experInfo;
                }).collect(Collectors.toList());
        ExperInfoPage experInfoPage = new ExperInfoPage();
        experInfoPage.setRecordCount((int)experIPage.getTotal()).setExperInfoList(experInfoList);
        return experInfoPage;
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

    @Override
    public List<GroupInfo> queryAllGroups(String experId) {
        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exper_id", experId);
        List<Group> groupList = groupMapper.selectList(queryWrapper);
        if(groupList.size() == 0){
            throw new NullPointerException("该实验id下没有分组");
        }
        List<GroupInfo> groupInfoList = groupList.stream()
                .map(group ->
                {
                    GroupInfo groupInfo = new GroupInfo();
                    groupInfo.setGroupName(group.getTitle()).setGroupId(group.getId());
                    return groupInfo;
                }).collect(Collectors.toList());
        return groupInfoList;
    }

    @Transactional
    @Override
    public String submitNonProg(String userId, List<UserAnswerDto> answers) {
        QueryWrapper<Submit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if(submitMapper.selectList(queryWrapper).size() != 0){
            throw new ElementDuplicateException("提交用户存在，提交错误");
        }
        // 去重
        Set<String> idSet = new HashSet<>();
        for(UserAnswerDto u : answers){
            if(idSet.contains(u.getQuestionId())){
                throw new ElementDuplicateException("提交题号重复");
            }
            idSet.add(u.getQuestionId());
        }
        // 入库
        answers.stream().forEach(u->
        {
            Submit submit = new Submit();
            submit.setUserId(userId).setQuestionId(u.getQuestionId()).setUserAnswer(u.getReply());
            submitMapper.insert(submit);
        });
        return userId;
    }
}
