package com.kse.lpep.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kse.lpep.common.exception.*;
import com.kse.lpep.mapper.*;
import com.kse.lpep.mapper.pojo.*;
import com.kse.lpep.service.IExperService;
import com.kse.lpep.service.dto.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private IUserMapper userMapper;

    /*
   1. 首先判断实验是否结束，结束则修改用户做题状态
   2. 如果实验没有结束，判断下一阶段题目类型
    */
    @Transactional
    @Override
    public NextPhaseStatusResult acquirePhaseStatus(String userId, String experId, int phaseNumber) {
        // 查询表t_phase
        NextPhaseStatusResult nextPhaseStatusResult = new NextPhaseStatusResult();
        Phase phase = phaseMapper.selectByExperAndNumber(experId, phaseNumber - 1);
        Phase phase1 = phaseMapper.selectByExperAndNumber(experId, phaseNumber);
        if(phaseNumber > 1 && phase == null){
            throw new NullPointerException("前端传入阶段错误");
        }
        if(phase1 == null){
            nextPhaseStatusResult.setIsEnd(1);
            modifyUserFootprint(userId, experId,
                    phaseNumber - 1, -1, 1);
        }else{
            nextPhaseStatusResult.setIsEnd(0).setIsProg(phase1.getType());
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

    /**
     * 获取非编程题问题
     * 情况1：用户中断之后回来
     * 情况2：用户正常提交
     * @param userId 用户id
     * @param experId 实验id
     * @param phaseNumber 实验阶段
     * @return
     */
    @Transactional
    @Override
    public List<NonProgQuestionInfo> acquireNonProgQuestion(String userId,  String experId, int phaseNumber) {
        // 1.校验用户存在中断，true存在中断，false不存在
        boolean isBreak;
        try{
            isBreak = verifyBreak(userId, experId, phaseNumber, -1);
        } catch (ElementDuplicateException e){
            throw new ElementDuplicateException(e.getMessage());
        }
        // 2.校验实验是否存在，用户是否在该实验中，该实验的phaseNumber阶段是否存在，并为非编程题
        List<String> groupPhaseId;
        try{
            groupPhaseId = verifyCondition(userId, experId, phaseNumber, false);
        }catch (RecordNotExistException | RecordConflictException e){
            throw new RecordNotExistException(e.getMessage());
        }
        // 3.获取问题列表并排序封装
        String groupId = groupPhaseId.get(0);
        String phaseId = groupPhaseId.get(1);
        List<Question> questions = questionMapper.selectByGroupPhase(groupId, phaseId);
        if(questions.size() == 0){
            throw new QuestionNotExistException("该非编程阶段没有题目");
        }
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
        // 4.修改用户状态，只有不存在中断并且第一个阶段才创建
        if(!isBreak && phaseNumber == 1){
            if(userFootprintMapper.selectByUserExper(userId, experId) != null){
                throw new ElementDuplicateException("userFootprint表中元素重复");
            }
            UserFootprint newUserFootprint = new UserFootprint();
            newUserFootprint.setUserId(userId).setExperId(experId).setGroupId(groupId).setCurrentPhaseNumber(1)
                    .setIsEnd(0).setCurrentQuestionNumber(-1);
            userFootprintMapper.insert(newUserFootprint);
        }
//            else{
//                if(userFootprint == null){
//                    throw new RecordNotExistException("userFootprint表中记录不存在");
//                }
//                modifyUserFootprint(userId, experId, phaseNumber, -1, 0);
//            }
//        // 1.获取groupId和phaseId并判空和题目类型
//        QueryWrapper<UserGroup> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_id", userId).eq("exper_id", experId);
//        String groupId = userGroupMapper.selectOne(queryWrapper).getGroupId();
//        QueryWrapper<Phase> queryWrapper1 = new QueryWrapper<>();
//        queryWrapper1.eq("exper_id", experId).eq("phase_number", phaseNumber);
//        Phase phase = phaseMapper.selectOne(queryWrapper1);
//        if(groupId == null || phase.getId() == null || phase.getType() == 1){
//            throw new NullPointerException();
//        }
//        // 2.获取问题列表
//        QueryWrapper<Question> queryWrapper2 = new QueryWrapper<>();
//        queryWrapper2.eq("group_id", groupId).eq("phase_Id", phase.getId());
//        List<Question> questions = questionMapper.selectList(queryWrapper2);
        // 3.排序封装
//        Collections.sort(questions, (a, b) -> a.getNumber() - b.getNumber());
//        List<NonProgQuestionInfo> questionInfos = questions.stream()
//                .map(question ->
//                {
//                    NonProgQuestionInfo questionInfo = new NonProgQuestionInfo();
//                    List<String> options = JSON.parseArray(question.getOptions(), String.class);
//                    questionInfo.setQuestionId(question.getId()).setContent(question.getContent())
//                            .setNumber(question.getNumber()).setOptions(options)
//                            .setType(question.getType()).setRemark(question.getRemark());
//                    return questionInfo;
//                }).collect(Collectors.toList());
//         4.更改用户状态
//         如果用户是中断状态进来的，那么此时不可以新建
//        if(phaseNumber == 1){
//            // 创建一个对象
//            UserFootprint userFootprint = new UserFootprint();
//            userFootprint.setUserId(userId).setExperId(experId).setGroupId(groupId).setCurrentPhaseNumber(1)
//                    .setIsEnd(0);
//            userFootprintMapper.insert(userFootprint);
//        }else{
//            // 修改状态，其中非编程题当前题号为null，实验不结束
//            modifyUserFootprint(userId, experId, phaseNumber, null, 0);
//        }
        return questionInfos;
    }

    /**
     * 前端获取题目（包括编程题和非编程题）时判断该用户是否存在中断
     * 非编程题题号固定赋值为-1
     * @param userId
     * @param experId
     * @param phaseNumber
     * @param questionNumber
     * @return
     */
    private boolean verifyBreak(String userId, String experId, int phaseNumber, int questionNumber){
        QueryWrapper<UserFootprint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("exper_id", experId).eq("current_phase_number", phaseNumber);
        if(questionNumber != -1){
            queryWrapper.eq("current_question_number", questionNumber);
        }
        System.out.println(1);
        List<UserFootprint> userFootprints = userFootprintMapper.selectList(queryWrapper);
        System.out.println(userFootprints.size());
        if(userFootprints.size() == 0){
            return false;
        }else if(userFootprints.size() == 1){
            return true;
        }else{
            throw new ElementDuplicateException("用户记录表出现多条记录，数据库出错");
        }
    }

    /**
     * 校验前端的数据并返回groupId和phaseId
     * @param userId
     * @param experId
     * @param phaseNumber
     */
    private List<String> verifyCondition(String userId, String experId, int phaseNumber, boolean isProg){
        List<String> ans = new ArrayList<>();
        // 校验用户和实验是否存在
        if(userMapper.selectById(userId) == null || experMapper.selectById(experId) == null){
            throw new RecordNotExistException("用户不存在或实验不存在");
        }
        // 校验用户是否存在该实验分组
        List<UserGroup> userGroups = userGroupMapper.getByUserIdAndExperId(userId, experId);
        if(userGroups.size() != 1){
            throw new RecordNotExistException("用户和实验分组不匹配");
        }else{
            ans.add(userGroups.get(0).getGroupId());
        }
        // 校验阶段是否存在
        Phase phase = phaseMapper.selectByExperAndNumber(experId, phaseNumber);
        if(StringUtils.isBlank(phase.getId())){
            throw new RecordNotExistException("实验阶段不存在");
        }
        // 校验阶段和题型是否对应
        if(!isProg && phase.getType() != 0){
            throw new RecordConflictException("题目类型和数据库不匹配");
        }
        if(isProg && phase.getType() != 1){
            throw new RecordConflictException("题目类型和数据库不匹配");
        }
        ans.add(phase.getId());
        return ans;
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
        questionNumber = questionNumber == -1 ? 1 : questionNumber;
        boolean isBreak;
        try{
            isBreak = verifyBreak(userId, experId, phaseNumber, questionNumber);
        }catch (ElementDuplicateException e){
            throw new ElementDuplicateException(e.getMessage());
        }
        // 2.校验实验是否存在，用户是否在该实验中等
        List<String> groupPhaseId;
        try{
            groupPhaseId = verifyCondition(userId, experId, phaseNumber, true);
        }catch (RecordConflictException | RecordNotExistException e){
            throw new RecordNotExistException(e.getMessage());
        }
        // 3.获取问题与封装
        String groupId = groupPhaseId.get(0);
        String phaseId = groupPhaseId.get(1);
        QueryWrapper<ProgQuestion> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("group_id", groupId).eq("phase_id", phaseId)
                .eq("number", questionNumber);
        QueryWrapper<ProgQuestion> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("group_id", groupId).eq("phase_id", phaseId)
                .eq("number", questionNumber + 1);
        ProgQuestion progQuestion = progQuestionMapper.selectOne(queryWrapper1);
        if(progQuestion == null){
            throw new QuestionNotExistException("请求的编程题目不存在，请开始下一阶段");
        }
        ProgQuestionResult progQuestionResult = new ProgQuestionResult();
        if(progQuestionMapper.selectOne(queryWrapper2) == null){
            progQuestionResult.setIsLast(1);
        }
        progQuestionResult.setQuestionId(progQuestion.getId()).setNumber(progQuestion.getNumber())
                .setContent(progQuestion.getContent()).setRemark(progQuestion.getRemark())
                .setRuntimeLimit(progQuestion.getRuntimeLimit()).setTimeLimit(progQuestion.getTimeLimit());

        // 3.修改用户状态
        if(!isBreak && phaseNumber == 1 && questionNumber == 1){
                UserFootprint userFootprint = new UserFootprint();
                userFootprint.setUserId(userId).setExperId(experId).setGroupId(groupId).
                        setCurrentPhaseNumber(1).setCurrentQuestionNumber(1).setIsEnd(0);
                userFootprintMapper.insert(userFootprint);
            }
//            else {
//                modifyUserFootprint(userId, experId, phaseNumber, questionNumber, 0);
//            }
        return progQuestionResult;
    }
//    public ProgQuestionResult acquireProgQuestion(String userId, String experId,
//                                                  int phaseNumber, int questionNumber) {
//        // 1.查表判空
//        try {
//            QueryWrapper<UserGroup> queryWrapper0 = new QueryWrapper<>();
//            queryWrapper0.eq("user_id", userId).eq("exper_id", experId);
//            String groupId = userGroupMapper.selectOne(queryWrapper0).getGroupId();
//            QueryWrapper<Phase> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("exper_id", experId).eq("phase_number", phaseNumber);
//            Phase phase = phaseMapper.selectOne(queryWrapper);
//            QueryWrapper<ProgQuestion> queryWrapper1 = new QueryWrapper<>();
//            queryWrapper1.eq("group_id", groupId).eq("phase_id", phase.getId())
//                    .eq("number", questionNumber);
//            QueryWrapper<ProgQuestion> queryWrapper2 = new QueryWrapper<>();
//            queryWrapper2.eq("group_id", groupId).eq("phase_id", phase.getId())
//                    .eq("number", questionNumber + 1);
//            // 2.获取问题列表并封装
//            ProgQuestion progQuestion = progQuestionMapper.selectOne(queryWrapper1);
//            ProgQuestionResult progQuestionResult = new ProgQuestionResult();
//            if(progQuestionMapper.selectOne(queryWrapper2) == null){
//                progQuestionResult.setIsLast(1);
//            }
//            progQuestionResult.setQuestionId(progQuestion.getId()).setNumber(progQuestion.getNumber())
//                    .setContent(progQuestion.getContent()).setRemark(progQuestion.getRemark())
//                    .setRuntimeLimit(progQuestion.getRuntimeLimit()).setTimeLimit(progQuestion.getTimeLimit());
//            // 3.修改用户状态
//            if(phaseNumber == 1 && questionNumber == 1){
//                UserFootprint userFootprint = new UserFootprint();
//                // 这里没有插入组别
//                userFootprint.setUserId(userId).setExperId(experId).setCurrentPhaseNumber(1)
//                        .setCurrentQuestionNumber(1).setIsEnd(0);
//                userFootprintMapper.insert(userFootprint);
//            }
//            modifyUserFootprint(userId, experId, phaseNumber, questionNumber, 0);
//            return progQuestionResult;
//        }catch (NullPointerException e){
//            // 如果出现空，说明前端数据传输错误或者非编程题
//            throw new NullPointerException();
//        }
//    }

    @Override
    public ExperInfoPage getAllExper(int pageIndex, int pageSize) {
        QueryWrapper<Exper> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        Page<Exper> experPage = new Page<>(pageIndex, pageSize, true);
        IPage<Exper> experIPage = experMapper.selectPage(experPage, queryWrapper);
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
                    .or(wp -> wp.eq(Exper::getState, 2).notIn(Exper::getId, experIds))
                    .orderByDesc(Exper::getCreateTime);
//            lambdaQueryWrapper.and(wp -> wp.eq(Exper::getState, 0).notIn(Exper::getId, experIds))
//                    .or(wp -> wp.eq(Exper::getState, 2).notIn(Exper::getId, experIds));
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

    private void modifyUserFootprint(String userId, String experId, int currentPhaseNumber,
                                     int currentQuestionNumber, int isEnd) {
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
    public String submitNonProg(String userId, String experId, int phaseNumber, List<UserAnswerDto> answers) {
        // 1.先校验数据
        try{
            verifyCondition(userId, experId, phaseNumber, false);
        }catch (RecordNotExistException | RecordConflictException e){
            throw new RecordNotExistException(e.getMessage());
        }

//        QueryWrapper<Submit> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_id", userId);
//        if(submitMapper.selectList(queryWrapper).size() != 0){
//            throw new ElementDuplicateException("用户已经提交过该部分题，提交错误");
//        }
//
//        // 去重
//        Set<String> idSet = new HashSet<>();
//        for(UserAnswerDto u : answers){
//            if(idSet.contains(u.getQuestionId())){
//                throw new ElementDuplicateException("提交题号重复错误");
//            }
//            idSet.add(u.getQuestionId());
//        }

        // 入库
        answers.stream().forEach(u->
        {
            Submit submit = new Submit();
            if(questionMapper.selectById(u.getQuestionId()) == null){
                throw new RecordNotExistException("提交的问题不存在");
            }
            if( submitMapper.selectByUserQuestion(userId, u.getQuestionId()).size() != 0){
                throw new ElementDuplicateException("问题重复错误");
            }
            submit.setUserId(userId).setQuestionId(u.getQuestionId()).setUserAnswer(u.getReply());
            submitMapper.insert(submit);
        });
        // 提交时需要修改状态，这里不做结束判断，所有的结束判断都由获取题型做
        modifyUserFootprint(userId, experId, phaseNumber + 1, -1, 0);
        return userId;
    }

}
