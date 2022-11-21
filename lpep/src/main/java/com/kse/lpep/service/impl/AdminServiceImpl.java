package com.kse.lpep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kse.lpep.common.exception.ExperNameDuplicateException;
import com.kse.lpep.common.exception.InsertException;
import com.kse.lpep.controller.vo.AddNonProgQuestionInfo;
import com.kse.lpep.controller.vo.CreateGroupInfo;
import com.kse.lpep.controller.vo.CreatePhaseInfo;
import com.kse.lpep.mapper.*;
import com.kse.lpep.mapper.pojo.*;
import com.kse.lpep.service.IAdminService;
import com.kse.lpep.service.dto.CreateExperResult;
import com.kse.lpep.service.dto.ExperInfo;
import com.kse.lpep.service.dto.NonProgQuestionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

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
    /*
     * 1.判断userId是否存在
     * 2.判断experId和groupId是否对应
     * 3.判断该用户是否已经加入
     */
    @Override
    public int addTesterToExper(String userId, String experId, String groupId) {
        if(userMapper.selectById(userId) == null){
            return 0;
        }
        String myExperId = groupMapper.selectById(groupId).getExperId();
        if(!myExperId.equals(experId)){
            return 0;
        }
        QueryWrapper<UserGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("group_id", groupId).eq("exper_id", experId);
        if(userGroupMapper.selectOne(queryWrapper) != null){
            return 0;
        }
        UserGroup userGroup = new UserGroup();
        userGroup.setUserId(userId).setGroupId(groupId).setExperId(experId);
        return userGroupMapper.insert(userGroup);
    }

    @Override
    public CreateExperResult createExper(String creatorId, String experName, String startTime, String workspace,
                                         List<CreateGroupInfo> groupInfoList, List<CreatePhaseInfo> phaseInfoList) {
        QueryWrapper<Exper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", experName);
        if(experMapper.selectOne(queryWrapper) != null){
            throw new ExperNameDuplicateException("该实验名称重复");
        }
        Exper exper = new Exper();
        Timestamp myStartTime = Timestamp.valueOf(startTime);
        exper.setTitle(experName).setCreator(creatorId).setState(0).setWorkspace(workspace)
                .setStartTime(myStartTime);
        try {
            experMapper.insert(exper);
        }catch (Exception e){
            throw new InsertException("实验数据插入错误");
        }
        Exper myReturnExper = experMapper.selectOne(queryWrapper);
        String experId = myReturnExper.getId();
        groupInfoList.stream().forEach(groupInfo->
        {
            Group group = new Group();
            group.setExperId(experId).setTitle(groupInfo.getGroupName());
            try{
                groupMapper.insert(group);
            }catch (Exception e){
                throw new InsertException("实验数据插入错误");
            }
        });

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
        CreateExperResult createExperResult = new CreateExperResult();
        createExperResult.setExperId(experId).setExperName(myReturnExper.getTitle())
                .setStartTime(startTime).setStatus(0);
        return createExperResult;
    }

    @Override
    public int addQuestionTypeNonProg(String experId, String groupName, String phaseNumber,
                                          List<AddNonProgQuestionInfo> addNonProgQuestionInfoList) {
        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exper_id", experId).eq("title", groupName);
        String groupId = groupMapper.selectOne(queryWrapper).getId();
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
                    question.setPhaseId(phaseId).setNumber(myItem.getNumber()).setContent(myItem.getContent())
                            .setOptions(myItem.getOptions()).setAnswer(myItem.getAnswer()).setGroupId(groupId)
                            .setExperId(experId).setType(myItem.getType());
                    questionMapper.insert(question);
                });
        return 1;
    }
}
