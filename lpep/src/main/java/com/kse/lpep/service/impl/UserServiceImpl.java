package com.kse.lpep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kse.lpep.mapper.IExperMapper;
import com.kse.lpep.mapper.IUserFootprintMapper;
import com.kse.lpep.mapper.IUserGroupMapper;
import com.kse.lpep.mapper.IUserMapper;
import com.kse.lpep.mapper.pojo.Exper;
import com.kse.lpep.mapper.pojo.User;
import com.kse.lpep.mapper.pojo.UserFootprint;
import com.kse.lpep.mapper.pojo.UserGroup;
import com.kse.lpep.service.IUserService;
import com.kse.lpep.service.dto.ExperInfo;
import com.kse.lpep.service.dto.TesterInfo;
import com.kse.lpep.service.dto.UserLoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        try{
            User user = userMapper.selectOne(queryWrapper);
            // 情况1：用户账号密码错误
            if(!user.getPassword().equals(password)){
                userLoginResult.setState(1);
            }
            // 情况2：用户账号密码正确，正常登录
            else {
                userLoginResult.setState(2).setId(user.getId()).setUserName(user.getUsername())
                        .setRealName(user.getRealname()).setIsAdmin(user.getIsAdmin());
            }
        }catch (NullPointerException  e){
            // 情况0：登录的用户不存在
            userLoginResult.setState(0);
        }finally {
            return userLoginResult;
        }
    }
    @Override
    public TesterInfo testerBasicInfo(String id) {
        TesterInfo testerInfo = new TesterInfo();
        try{
            // 情况2：正常查询并返回
            User user = userMapper.selectById(id);
            testerInfo.setUserName(user.getUsername()).setRealName(user.getRealname()).setState(1)
                    .setCreateTime(user.getCreateTime()).setMsg("查询成功");
        }catch (NullPointerException e){
            // 情况1：查询用户主键不存在
            testerInfo.setState(0);
        } finally {
            return testerInfo;
        }
    }
//    public List<ExperInfo> experToParticipate(String id) {
//        // 记录最后的结果
//        List<ExperInfo> list = new ArrayList<>();
//        User user = new User();
//        try{
//            user = userMapper.selectById(id);
//        }catch (MybatisPlusException e){
//            // 情况2：用户不存在
//            ExperInfo experInfo = new ExperInfo();
//            experInfo.setState(2);
//            list.add(experInfo);
//            return list;
//        }
//        if(user == null){
//            // 情况2：用户不存在
//            ExperInfo experInfo = new ExperInfo();
//            experInfo.setState(2);
//            list.add(experInfo);
//            return list;
//        }
//        if(user.getCurrentPhaseId() != null){
//            // 情况0：用户存在中断的实验
//            ExperInfo experInfo = new ExperInfo();
//            experInfo.setId(user.getId());
//            experInfo.setStartTime(user.getCurrentStart());
//            experInfo.setState(0);
//            experInfo.setCurrentPhaseId(user.getCurrentPhaseId());
//            experInfo.setCurrentQuestionId(user.getCurrentQuestionId());
//            list.add(experInfo);
//            return list;
//        }else{
//            // 情况1：正常情况
//            // 先查询表t_user_group查看用户所参与的所有实验
//            QueryWrapper<UserGroup> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("user_id", id);
//            // 记录查询结果
//            List<UserGroup> userGroups = new ArrayList<>();
//            try{
//                userGroups = userGroupMapper.selectList(queryWrapper);
//            }catch (MybatisPlusException e){
//                // 查询出错，返回空即可
//                return list;
//            }
//            // 查询出所有的实验id
//            List<String> experIds = userGroups.stream()
//                    .map(UserGroup::getExperId)
//                    .collect(Collectors.toList());
//            // 查询每个实验id的状态，满足则返回最终结果
//            List<Exper> expers = new ArrayList<>();
//            for(String experId : experIds){
//                try{
//                    Exper exper = experMapper.selectById(experId);
//                    expers.add(exper);
//                }catch (MybatisPlusException e){
//                    // 查询的实验不存在，应该不存在这个问题
//                }
//            }
//            for(Exper exper : expers){
//                if(exper.getState() == 2){
//                    // 在表t_user_accomplish中验证用户是否完成这个实验
//                    QueryWrapper<UserFootprint> queryWrapper1 = new QueryWrapper<>();
//                    queryWrapper1.eq("user_id", id).eq("exper_id", exper.getId());
//                    UserFootprint userFootprint = new UserFootprint();
//                    try{
//                        userFootprint = userAccomplishMapper.selectOne(queryWrapper1);
//                    }catch (RuntimeException e){
//                        // 上面这块存在一个查找空表找不到的异常，强行用RuntimeException来接这个异常
//                    }
//                    if(userFootprint.getId() == null){
//                        ExperInfo experInfo = new ExperInfo();
//                        experInfo.setId(exper.getId());
//                        experInfo.setTitle(exper.getTitle());
//                        experInfo.setStartTime(exper.getStartTime());
//                        experInfo.setState(1);
//                        list.add(experInfo);
//                    }
//                }
//            }
//            return list;
//        }
//    }
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
    public List<ExperInfo> experToParticipate(String id) throws NullPointerException{
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
                if(userFootprint.getIsComplete() == 1){
                    continue;
                }
                // 情况1：存在实验中断
                experInfo.setCurrentPhaseId(userFootprint.getCurrentPhaseId()).setCurrentStartTime(userFootprint.getStartTime())
                        .setCurrentQuestionId(userFootprint.getCurrentQuestionId()).setState(1);
                isExistBreak = true;
            }
            // 情况2：正常情况，正常状态为1，存在中断状态为2
            Exper exper = experMapper.selectById(experId);
            experInfo.setId(experId).setTitle(exper.getTitle()).setStartTime(exper.getStartTime())
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

//    @Override
//    public boolean testException(String id) throws NullPointerException{
//        User user = userMapper.selectById(id);
//        if(user.getIsAdmin() == 1){
//            return true;
//        }
//        return false;
//    }
}
