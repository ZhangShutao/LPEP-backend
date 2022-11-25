package com.kse.lpep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kse.lpep.mapper.IGroupMapper;
import com.kse.lpep.mapper.IUserGroupMapper;
import com.kse.lpep.mapper.IUserMapper;
import com.kse.lpep.mapper.pojo.User;
import com.kse.lpep.mapper.pojo.UserGroup;
import com.kse.lpep.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements IAdminService {
    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private IGroupMapper groupMapper;
    @Autowired
    private IUserGroupMapper userGroupMapper;
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
        UserGroup userGroup = new UserGroup(userId, groupId, experId);
        return userGroupMapper.insert(userGroup);
    }
}
