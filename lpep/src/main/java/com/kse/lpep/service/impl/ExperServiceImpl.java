package com.kse.lpep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kse.lpep.mapper.IPhaseMapper;
import com.kse.lpep.mapper.IUserGroupMapper;
import com.kse.lpep.mapper.pojo.Phase;
import com.kse.lpep.mapper.pojo.UserGroup;
import com.kse.lpep.service.IExperService;
import com.kse.lpep.service.dto.QuestionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperServiceImpl implements IExperService {
    @Autowired
    private IUserGroupMapper userGroupMapper;

    @Autowired
    private IPhaseMapper phaseMapper;

    /*
    1. 获取该实验的groupId和phaseId并判空
    2. 根据步骤1和步骤2中的数据查询表t_question获取问题列表
     */
    @Override
    public List<QuestionInfo> getNonProgQuestion(String userId, String experId, int phaseNumber) {
        // 1.获取groupId和phaseId并判空
        QueryWrapper<UserGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("exper_id", experId);
        String groupId = userGroupMapper.selectOne(queryWrapper).getGroupId();
        QueryWrapper<Phase> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("exper_id", experId).eq("phase_number", phaseNumber);
        String phaseId = phaseMapper.selectOne(queryWrapper1).getId();
        if(groupId == null || phaseId == null){
            throw new NullPointerException();
        }
        // 2.获取问题列表



        return null;
    }
}
