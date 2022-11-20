package com.kse.lpep.service;

import com.kse.lpep.service.dto.ExperInfo;
import com.kse.lpep.service.dto.PersonalResult;
import com.kse.lpep.service.dto.TesterInfo;
import com.kse.lpep.service.dto.UserLoginResult;

import java.util.List;

public interface IUserService {
    /**
     * 用户登录
     * @param username 用户账号
     * @param password 用户密码
     * @return 用户的个人信息
     */
    UserLoginResult userLogin(String username, String password);

    /**
     * 用户查询基本信息
     * @param id 用户唯一标识id
     * @return 用户个人基本信息
     */
    PersonalResult personalBasicInfo(String id);

    /**
     * 用户查询待参与的实验
     * @param id 用户唯一标识id
     * @return 用户待参与的实验
     */
    List<ExperInfo> expersToParticipate(String id);

    /**
     * 查询所有tester的个人信息
     */
    List<TesterInfo> queryAllTester();

    /**
     * 创建新用户
     */
    int createNewUser(String username, String realname, int isAdmin);

//    /**
//     * 指定id的用户是否存在，主要用来检验
//     * true表示用户存在，false表示用户不存在
//     */
//    boolean isUserExist(String userId);

    /**
     * 管理员删除用户，1表示成功，0表示失败
     * @param userId
     * @return
     */
    int deleteUser(String userId);

}
