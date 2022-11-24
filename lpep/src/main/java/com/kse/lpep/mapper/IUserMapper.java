package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IUserMapper extends BaseMapper<User> {
    @Select("select * from t_user where username=#{username} and password=#{password}")
    User selectAccountPassword(@Param("username") String username, @Param("password") String password);
}
