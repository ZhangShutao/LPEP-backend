package com.kse.lpep.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kse.lpep.common.exception.ElementDuplicateException;
import com.kse.lpep.common.exception.InsertException;
import com.kse.lpep.controller.vo.AddNonProgQuestionInfo;
import com.kse.lpep.controller.vo.AddProgQuestionInfo;
import com.kse.lpep.controller.vo.CreateGroupInfo;
import com.kse.lpep.controller.vo.CreatePhaseInfo;
import com.kse.lpep.mapper.*;
import com.kse.lpep.mapper.pojo.*;
import com.kse.lpep.service.IAdminService;
import com.kse.lpep.service.dto.AddProgQuestionDto;
import com.kse.lpep.service.dto.CreateExperResult;
import com.kse.lpep.service.dto.GroupInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.FileSystems;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements IAdminService {
    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private IGroupMapper groupMapper;
    @Autowired
    private IUserGroupMapper userGroupMapper;
    @Autowired
    private IExperMapper experMapper;
    @Autowired
    private IPhaseMapper phaseMapper;
    @Autowired
    private IQuestionMapper questionMapper;
    @Autowired
    private IProgQuestionMapper progQuestionMapper;
    @Autowired
    private ICaseMapper caseMapper;
    @Value("${is_windows}")
    private Integer isWindows;
    /*
     * 1.判断userId是否存在
     * 2.判断experId和groupId是否对应
     * 3.判断该用户是否已经加入
     */
    @Transactional
    @Override
    public int addTesterToExper(String userId, String experId, String groupId) {
        if(userMapper.selectById(userId) == null){
            throw new NullPointerException("用户id不存在");
        }
        String myExperId = groupMapper.selectById(groupId).getExperId();
        if(!myExperId.equals(experId)){
            throw new NullPointerException("实验id和组别id错误");
        }
        QueryWrapper<UserGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("group_id", groupId).eq("exper_id", experId);
        if(userGroupMapper.selectOne(queryWrapper) != null){
            throw new ElementDuplicateException("用户已存在");
        }
        UserGroup userGroup = new UserGroup();
        userGroup.setUserId(userId).setGroupId(groupId).setExperId(experId);
        return userGroupMapper.insert(userGroup);
    }

    @Transactional
    @Override
    public CreateExperResult createExper(String creatorId, String experName, String startTime, String workspace,
                                         List<CreateGroupInfo> groupInfoList, List<CreatePhaseInfo> phaseInfoList) {
        QueryWrapper<Exper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", experName);
        if(experMapper.selectOne(queryWrapper) != null){
            throw new ElementDuplicateException("该实验名称重复");
        }
        Exper exper = new Exper();
        Timestamp myStartTime = Timestamp.valueOf(startTime);
        String fileSeparator = FileSystems.getDefault().getSeparator();
        if(isWindows == 1) {
            workspace = "c:" + fileSeparator + workspace;
        }
        exper.setTitle(experName).setCreator(creatorId).setState(0).setWorkspace(workspace)
                .setStartTime(myStartTime);
        try {
            experMapper.insert(exper);
        }catch (Exception e){
            throw new InsertException("实验数据插入错误");
        }
        Exper myReturnExper = experMapper.selectOne(queryWrapper);
        String experId = myReturnExper.getId();
        List<GroupInfo> groupInfos = groupInfoList.stream()
            .map(g ->
            {
                Group group = new Group();
                group.setExperId(experId).setTitle(g.getGroupName());
                try{
                    groupMapper.insert(group);
                }catch (Exception e){
                    throw new InsertException("实验数据插入错误");
                }
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupName(g.getGroupName()).setGroupId(group.getId());
                return groupInfo;
            }).collect(Collectors.toList());

        phaseInfoList.stream().forEach(phaseInfo->
        {
            Phase phase = new Phase();
            phase.setName(phaseInfo.getPhaseName()).setExperId(experId)
                    .setPhaseNumber(phaseInfo.getNumber()).setType(phaseInfo.getPhaseType());
            try{
                phaseMapper.insert(phase);
            }catch (Exception e){
                throw new InsertException("实验数据插入错误");
            }
        });
//        QueryWrapper<Group> queryWrapper1 = new QueryWrapper<>();
//        queryWrapper1.eq("exper_id", experId);
//        List<String> groupIds = groupMapper.selectList(queryWrapper1).stream()
//                .map(Group::getId).collect(Collectors.toList());
        CreateExperResult createExperResult = new CreateExperResult();
        createExperResult.setExperId(experId).setExperName(myReturnExper.getTitle())
                .setStartTime(startTime).setStatus(0).setGroups(groupInfos);
        return createExperResult;
    }

    @Transactional
    @Override
    public int addQuestionTypeNonProg(String experId, String groupId, int phaseNumber,
                                          List<AddNonProgQuestionInfo> addNonProgQuestionInfoList) {
//        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("exper_id", experId).eq("title", groupName);
//        String groupId = groupMapper.selectOne(queryWrapper).getId();
        QueryWrapper<Phase> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("exper_id", experId).eq("phase_number", phaseNumber);
        String phaseId = phaseMapper.selectOne(queryWrapper1).getId();
        addNonProgQuestionInfoList.stream()
                .forEach(myItem ->
                {

                    QueryWrapper<Question> queryWrapper2 = new QueryWrapper<>();
                    queryWrapper2.eq("phase_id", phaseId).eq("number", myItem.getNumber())
                            .eq("group_id", groupId);
                    if(questionMapper.selectOne(queryWrapper2) != null){
                        throw new InsertException("问题插入重复");
                    }
                    Question question = new Question();
                    String options = JSON.toJSONString(myItem.getOptions());
                    question.setPhaseId(phaseId).setNumber(myItem.getNumber()).setContent(myItem.getContent())
                            .setOptions(options).setAnswer(myItem.getAnswer()).setGroupId(groupId)
                            .setExperId(experId).setType(myItem.getType());
                    questionMapper.insert(question);
                });
        return 1;
    }

//    @Override
//    public int addQuestionTypeProg(AddProgQuestionDto reqDto, List<String> caseIds) {
//        try {
//            // 1.获取组别id和阶段id
//            QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("exper_id", reqDto.getExperId()).eq("title", reqDto.getGroupName());
//            String groupId = groupMapper.selectOne(queryWrapper).getId();
//            QueryWrapper<Phase> queryWrapper1 = new QueryWrapper<>();
//            queryWrapper1.eq("exper_id", reqDto.getExperId())
//                    .eq("phase_number", reqDto.getPhaseNumber());
//            String phaseId = phaseMapper.selectOne(queryWrapper1).getId();
//
//            // 2.验证该题号是否存在
//            QueryWrapper<ProgQuestion> queryWrapper2 = new QueryWrapper<>();
//            queryWrapper2.eq("phase_id", phaseId).eq("number", reqDto.getQuestionNumber())
//                    .eq("group_id", groupId);
//            if(progQuestionMapper.selectOne(queryWrapper2) != null){
//                throw new ElementDuplicateException("问题插入重复");
//            }
//            ProgQuestion progQuestion = new ProgQuestion();
//            progQuestion.setPhaseId(phaseId).setNumber(reqDto.getQuestionNumber()).setContent(reqDto.getContent())
//                    .setGroupId(groupId).setExperId(reqDto.getExperId()).setRunnerId(reqDto.getRunnerId())
//                    .setTimeLimit(reqDto.getTimeLimit()).setRuntimeLimit(reqDto.getRuntimeLimit());
//            progQuestionMapper.insert(progQuestion);
//            // 3.更新case表
//            String progQuestionId = progQuestion.getId();
//            caseIds.stream().forEach(caseId->
//            {
//                if(caseMapper.selectById(caseId) == null){
//                    throw new NullPointerException("caseId列表传入错误");
//                }
//                Case myCase = new Case();
//                myCase.setId(caseId).setProgQuestionId(progQuestionId);
//                caseMapper.updateById(myCase);
//            });
//        }catch (NullPointerException e){
//            throw new NullPointerException("实验id，组别名，阶段编号传入错误");
//        }
//        return 1;
//    }
    @Transactional
    @Override
    public void addQuestionTypeProg(String experId, String groupId, int phaseNumber,
                                   List<AddProgQuestionInfo> addProgQuestionInfoList) {
        // 1.获取组别id和阶段id
//        String groupId = groupMapper.selectIdByExperAndName(experId, groupName);
        String phaseId = phaseMapper.selectByExperAndNumber(experId, phaseNumber).getId();
        if(StringUtils.isBlank(groupId) || StringUtils.isBlank(phaseId)){
            throw new NullPointerException("groupId 或 phaseId错误");
        }
        addProgQuestionInfoList.stream().forEach(q->{
            // 2.验证题号是否存在
            if(!StringUtils.isBlank(progQuestionMapper.selectIdByPhaseNumberGroup(phaseId,
                    q.getQuestionNumber(), groupId))){
                throw new ElementDuplicateException("问题插入重复");
            }
            ProgQuestion progQuestion = new ProgQuestion();
            progQuestion.setPhaseId(phaseId).setNumber(q.getQuestionNumber()).setContent(q.getContent())
                    .setGroupId(groupId).setExperId(experId).setRunnerId(q.getRunnerId())
                    .setTimeLimit(q.getTimeLimit()).setRuntimeLimit(q.getRuntimeLimit());
            progQuestionMapper.insert(progQuestion);
            // 3.更新case表
            String progQuestionId = progQuestion.getId();
            int caseNumber = 1;
            for(String caseId : q.getCaseIds()){
                if(caseMapper.selectById(caseId) == null){
                    throw new NullPointerException("问题" + progQuestionId + "的caseId列表传入错误");
                }
                Case myCase = new Case();
                myCase.setId(caseId).setProgQuestionId(progQuestionId).setNumber(caseNumber++);
                caseMapper.updateById(myCase);
            }

//            q.getCaseIds().stream().forEach(caseId ->
//            {
//                if(caseMapper.selectById(caseId) == null){
//                    throw new NullPointerException("问题" + progQuestionId + "的caseId列表传入错误");
//                }
//                Case myCase = new Case();
//                myCase.setId(caseId).setProgQuestionId(progQuestionId);
//                caseMapper.updateById(myCase);
//            });
        });
    }
}
