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
import com.kse.lpep.service.dto.*;
import org.apache.ibatis.javassist.bytecode.DuplicateMemberException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
            throw new NullPointerException("用户不存在");
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
            throw new NullPointerException("用户分组错误或培训材料错误");
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

    @Override
    public List<ExperGroupInfo> queryAllExperGroup() {
        List<Exper> experList = experMapper.selectList(null);
        List<ExperGroupInfo> experGroupInfoList = experList.stream()
                .map(exper ->
                {
                    QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("exper_id", exper.getId());
                    List<Group> groupList = groupMapper.selectList(queryWrapper);
                    List<GroupInfo> groupInfoList = groupList.stream()
                            .map(group ->
                            {
                                GroupInfo groupInfo = new GroupInfo();
                                groupInfo.setGroupId(group.getId()).setGroupName(group.getTitle());
                                return groupInfo;
                            }).collect(Collectors.toList());
                    ExperGroupInfo experGroupInfo = new ExperGroupInfo();
                    experGroupInfo.setExperId(exper.getId()).setExperName(exper.getTitle())
                            .setGroupInfoList(groupInfoList);
                    return experGroupInfo;
                }).collect(Collectors.toList());
        return experGroupInfoList;
    }



    @Override
    public int vaildStatus(String name, String experId, String groupId) {
        QueryWrapper<TrainingMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", name);
        // 错误情况1：培训教材名存在
        if(trainingMaterialMapper.selectOne(queryWrapper) != null){
            return 1;
        }
        QueryWrapper<UserGroup> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("exper_id", experId).eq("group_id", groupId);
        List<UserGroup> userGroups = userGroupMapper.selectList(queryWrapper1);
        // 错误情况2：实验名和组别名传入错误
        if(userGroups.size() == 0){
            return 2;
        }
        QueryWrapper<TrainingMaterial> queryWrapper2 = new QueryWrapper<>();
        queryWrapper1.eq("absolute_path", experId).eq("group_id", groupId);
        return 0;
    }

    @Override
    public QueryTrainingMaterialInfo createTrainingMaterial(String name, String experId, String groupId,
                                                       MultipartFile file) {
        String fileSeparator = FileSystems.getDefault().getSeparator();
        // 1.先插入数据库
        Exper exper = experMapper.selectById(experId);
        String workspace = exper.getWorkspace();
        String savePath = workspace + fileSeparator + "training" + fileSeparator;
        String absolutePath = savePath + file.getOriginalFilename();
        TrainingMaterial trainingMaterial = new TrainingMaterial();
        trainingMaterial.setGroupId(groupId).setTitle(name).setAbsolutePath(absolutePath);

        QueryWrapper<TrainingMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("absolute_path", absolutePath);
        if(trainingMaterialMapper.selectOne(queryWrapper) != null){
            throw new NullPointerException();
        }
        trainingMaterialMapper.insert(trainingMaterial);
        TrainingMaterial trainingMaterial2 = trainingMaterialMapper.selectOne(queryWrapper);

        QueryTrainingMaterialInfo queryTrainingMaterialInfo = new QueryTrainingMaterialInfo();

        String lastUpdateTime = new SimpleDateFormat("yyyy-MM-dd")
                .format(trainingMaterial2.getLastUpdateTime());
        String groupName = groupMapper.selectById(groupId).getTitle();
        queryTrainingMaterialInfo.setId(trainingMaterial2.getId()).setTitle(name)
                .setExperName(exper.getTitle()).setLastUpdateTime(lastUpdateTime);
        queryTrainingMaterialInfo.setGroupName(groupName);
        // 2.保存文件
        try{
            File files = new File(savePath, file.getOriginalFilename());
            file.transferTo(files);
        }catch (IOException  e) {
            throw new NullPointerException();
        }
        return queryTrainingMaterialInfo;
    }
}
