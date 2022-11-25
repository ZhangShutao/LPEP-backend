package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IUserMapper extends BaseMapper<User> {
    @Select("select id, username, password, realname, is_admin, create_time from t_user " +
            "where username=#{username}")
    User selectByUsername(@Param("username") String username);
}
