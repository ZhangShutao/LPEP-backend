package com.kse.lpep.service;

import com.kse.lpep.service.dto.*;

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
    TesterInfoPage queryAllTester(int pageIndex, int pageSize);

    /**
     * 创建新用户
     */
    TesterInfo createNewUser(String username, String realname, int isAdmin);

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

    /**
     * 根据用户账号获取用户真实姓名和id
     * @param username
     * @return
     */
    UserRealnameDto getRealname(String username);


    /**
     * 根据实验id获取所有用户和组别信息
     * @param experId
     * @return
     */
    UserWithGroupInfoPage getAllUserGroupByExperId(String experId, int pageIndex, int pageSize);

    /**
     * 管理员从实验中删除用户
     * @param experId
     * @param userId
     */
    void deleteUserFromExper(String experId, String userId);

}
