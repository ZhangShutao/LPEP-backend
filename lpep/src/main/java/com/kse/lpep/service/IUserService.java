package com.kse.lpep.service;

import com.kse.lpep.service.dto.ExperInfo;
import com.kse.lpep.service.dto.PersonalInfo;
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
    PersonalInfo personalBasicInfo(String id);

    /**
     * 用户查询待参与的实验
     * @param id 用户唯一标识id
     * @return 用户待参与的实验
     */
    List<ExperInfo> expersToParticipate(String id);
}
