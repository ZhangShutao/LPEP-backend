package com.kse.lpep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kse.lpep.mapper.IExperMapper;
import com.kse.lpep.mapper.IUserFootprintMapper;
import com.kse.lpep.mapper.IUserGroupMapper;
import com.kse.lpep.mapper.IUserMapper;
import com.kse.lpep.mapper.pojo.Exper;
import com.kse.lpep.mapper.pojo.User;
import com.kse.lpep.mapper.pojo.UserFootprint;
import com.kse.lpep.mapper.pojo.UserGroup;
import com.kse.lpep.service.IUserService;
import com.kse.lpep.service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserMapper userMapper;

    @Autowired
    private IUserGroupMapper userGroupMapper;

    @Autowired
    private IExperMapper experMapper;

    @Autowired
    private IUserFootprintMapper userFootprintMapper;

    @Override
    public UserLoginResult userLogin(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        UserLoginResult userLoginResult = new UserLoginResult();
        User user = userMapper.selectOne(queryWrapper);
        if(user == null || !(user.getPassword().equals(password))){
            System.out.println(user);
            throw new NullPointerException();
        }
        userLoginResult.setUserId(user.getId()).setUsername(user.getUsername())
                .setRealname(user.getRealname()).setIsAdmin(user.getIsAdmin());
        return userLoginResult;
    }

    @Override
    public PersonalResult personalBasicInfo(String id) {
        PersonalResult personalResult = new PersonalResult();
        User user = userMapper.selectById(id);
        if(user == null){
            throw new NullPointerException();
        }
        String myTime = new SimpleDateFormat("yyyy-MM-dd").format(user.getCreateTime());
        personalResult.setUsername(user.getUsername()).setRealname(user.getRealname()).setCreateTime(myTime);
        return personalResult;
    }

    /*
    情况0：用户不存在，直接返回null
    情况1：正常状态，用户可以选择其中一个待参与实验进行实验
    情况2：用户存在实验中断，此时用户只能点击中断的实验继续实验，其他待参与的实验能看到但不能开始
    1. 查询t_user表判断该用户是否存在
    2. 综合查询表t_user_group和表t_exper获取用户的所有实验（包括完成的和没有开始的）并保留正在进行的实验
    3. 查询表t_user_footprint，判断用户是否存在未完成的实验以及筛选已经完成的实验
    4. 返回最终结果
     */
    @Override
    public List<ExperInfo> expersToParticipate(String id){
        List<ExperInfo> list = new ArrayList<>();
        // 1.判断用户是否存在
        if(userMapper.selectById(id) == null){
            throw new NullPointerException();
        }
        QueryWrapper<UserGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        // 2.查询获取用户所有的实验id
        List<UserGroup> userGroups = userGroupMapper.selectList(queryWrapper);
        List<String> experIds = userGroups.stream()
                .filter(userGroup -> {
                    Exper exper = experMapper.selectById(userGroup.getExperId());
                    return exper.getState() == 2;
                }).map(UserGroup::getExperId)
                .collect(Collectors.toList());
        // 3.判断用户是否存在未完成的实验，并筛选已经完成的实验
        boolean isExistBreak = false;
        for(String experId : experIds){
            QueryWrapper<UserFootprint> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("user_id", id).eq("exper_id", experId);
            UserFootprint userFootprint = userFootprintMapper.selectOne(queryWrapper1);
            ExperInfo experInfo = new ExperInfo();
            experInfo.setState(0);
            if(userFootprint != null){
                if(userFootprint.getIsEnd() == 1){
                    continue;
                }
                // 情况1：存在实验中断
                String currentStartTime = new SimpleDateFormat("yyyy-MM-dd").format(userFootprint.getStartTime());
                experInfo.setCurrentPhaseNumber(userFootprint.getCurrentPhaseNumber())
                        .setCurrentStartTime(currentStartTime)
                        .setCurrentQuestionNumber(userFootprint.getCurrentQuestionNumber()).setState(1);
                isExistBreak = true;
            }
            // 情况2：正常情况，正常状态为1，存在中断状态为2
            Exper exper = experMapper.selectById(experId);
            String startTime = new SimpleDateFormat("yyyy-MM-dd").format(exper.getStartTime());
            experInfo.setExperId(experId).setTitle(exper.getTitle()).setStartTime(startTime)
                    .setState(experInfo.getState() + 1);
            list.add(experInfo);
            }
        if(isExistBreak){
            list.stream().map(experInfo ->
            {
                if(experInfo.getState() == 1){
                    experInfo.setState(0);
                }
                return experInfo;
            }).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public TesterInfoPage queryAllTester(int pageIndex, int pageSize) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_admin", 0);
        Page<User> userPage = new Page<>(pageIndex, pageSize, true);
        IPage<User> userIPage = userMapper.selectPage(userPage, queryWrapper);
        List<TesterInfo> testerInfoList = userIPage.getRecords().stream()
                .map(user -> {
                    TesterInfo testerInfo = new TesterInfo();
                    String createTime = new SimpleDateFormat("yyyy-MM-dd").format(user.getCreateTime());
                    testerInfo.setUserId(user.getId()).setUsername(user.getUsername()).setRealname(user.getRealname())
                            .setIsAdmin(user.getIsAdmin());
                    // 发现一个奇怪的问题，估计是IDEA的问题，在上面不能跟着写下面这行
                    testerInfo.setCreateTime(createTime);
                    return testerInfo;
                }).collect(Collectors.toList());
        TesterInfoPage testerInfoPage = new TesterInfoPage();
        testerInfoPage.setRecordCount((int)userIPage.getTotal()).setTesterInfoList(testerInfoList);
        return testerInfoPage;
    }

    @Override
    public TesterInfo createNewUser(String username, String realname, int isAdmin) {
        User user = new User();
        user.setUsername(username).setPassword(username).setRealname(realname).setIsAdmin(isAdmin);
        try{
            userMapper.insert(user);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            String userId = userMapper.selectOne(queryWrapper).getId();
            TesterInfo testerInfo = new TesterInfo();
            testerInfo.setUserId(userId).setUsername(username).setRealname(realname).setIsAdmin(isAdmin);
            // 用户账号唯一，捕获重复插入的异常
            return testerInfo;
        }catch (DuplicateKeyException e){
            return null;
        }
    }


//    @Override
//    public boolean isUserExist(String userId) {
//        User user = userMapper.selectById(userId);
//        if(user == null){
//            return false;
//        }
//        return true;
//    }


    /**
     * 管理员删除用户
     */
    @Override
    public int deleteUser(String userId) {
        return userMapper.deleteById(userId);
    }
}
