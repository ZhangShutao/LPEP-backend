package com.kse.lpep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kse.lpep.mapper.*;
import com.kse.lpep.mapper.pojo.Exper;
import com.kse.lpep.mapper.pojo.Group;
import com.kse.lpep.mapper.pojo.TrainingMaterial;
import com.kse.lpep.mapper.pojo.UserGroup;
import com.kse.lpep.service.ITrainingMaterialService;
import com.kse.lpep.service.dto.QueryTrainingMaterialInfo;
import com.kse.lpep.service.dto.QueryTrainingMaterialInfoPage;
import com.kse.lpep.service.dto.TrainingMaterialInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingMaterialServiceImpl implements ITrainingMaterialService {


    @Autowired
    private IUserGroupMapper userGroupMapper;
    @Autowired
    private IExperMapper experMapper;
    @Autowired
    private ITrainingMaterialMapper trainingMaterialMapper;
    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private IGroupMapper groupMapper;
    /**
     * 1.首先查询个人实验状态未结束的所有experId
     * 2.根据userId和experId获取groupId
     * 3.根据groupId返回trainingId，title，absolute_path
     */
    @Override
    public List<TrainingMaterialInfo> listPersonalTextbook(String userId) {
        if(userMapper.selectById(userId) == null){
            throw new NullPointerException();
        }
        QueryWrapper<UserGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        try {
            List<UserGroup> userGroups = userGroupMapper.selectList(queryWrapper);
            List<TrainingMaterialInfo> trainingMaterialInfos = userGroups.stream()
                    .filter(userGroup ->
                    {
                        Exper exper = experMapper.selectById(userGroup.getExperId());
                        return exper.getState() != 1;
                    })
                    .map(userGroup ->
                    {
                        String experTitle = experMapper.selectById(userGroup.getExperId()).getTitle();
                        TrainingMaterialInfo trainingMaterialInfo = new TrainingMaterialInfo();
                        String groupId = userGroup.getGroupId();
                        QueryWrapper<TrainingMaterial> queryWrapper1 = new QueryWrapper<>();
                        queryWrapper1.eq("group_id", groupId);
                        TrainingMaterial trainingMaterial = trainingMaterialMapper.selectOne(queryWrapper1);
                        String lastUpdateTime = new SimpleDateFormat("yyyy-MM-dd")
                                .format(trainingMaterial.getLastUpdateTime());

                        trainingMaterialInfo.setId(trainingMaterial.getId()).setTitle(trainingMaterial.getTitle())
                                .setExperName(experTitle).setLastUpdateTime(lastUpdateTime);
                        return trainingMaterialInfo;
                    }).collect(Collectors.toList());
            return trainingMaterialInfos;
        }catch (NullPointerException e){
            throw new NullPointerException();
        }
    }

    @Override
    public QueryTrainingMaterialInfoPage queryAllMaterialInfo(int pageIndex, int pageSize) {
        try{
            Page<TrainingMaterial> trainingMaterialPage = new Page<>(pageIndex, pageSize, true);
            IPage<TrainingMaterial> trainingMaterialIPage = trainingMaterialMapper
                    .selectPage(trainingMaterialPage, null);

            List<TrainingMaterial> trainingMaterialList = trainingMaterialIPage.getRecords();

            List<QueryTrainingMaterialInfo> queryTrainingMaterialInfoList = trainingMaterialList.stream()
                    .map(trainingMaterial ->
                    {
                        QueryTrainingMaterialInfo myItem = new QueryTrainingMaterialInfo();
                        String lastUpdateTime = new SimpleDateFormat("yyyy-MM-dd")
                                .format(trainingMaterial.getLastUpdateTime());
                        Group group = groupMapper.selectById(trainingMaterial.getGroupId());
                        String groupName = group.getTitle();
                        String experTitle = experMapper.selectById(group.getExperId()).getTitle();
                        myItem.setId(trainingMaterial.getId()).setTitle(trainingMaterial.getTitle())
                                .setLastUpdateTime(lastUpdateTime).setExperName(experTitle);
                        myItem.setGroupName(groupName);
                        return myItem;
                    }).collect(Collectors.toList());
            QueryTrainingMaterialInfoPage queryTrainingMaterialInfoPage = new QueryTrainingMaterialInfoPage();
            queryTrainingMaterialInfoPage.setRecordCount((int)trainingMaterialIPage.getTotal())
                    .setQueryTrainingMaterialInfoList(queryTrainingMaterialInfoList);
            return queryTrainingMaterialInfoPage;
        }catch (NullPointerException e){
            throw new NullPointerException();
        }
    }

    @Override
    public Integer removeTrainingMaterialById(String id) {
        return trainingMaterialMapper.deleteById(id);
    }
}
