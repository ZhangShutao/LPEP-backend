package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.UserFootprint;
import com.kse.lpep.mapper.pojo.UserGroup;
import lombok.experimental.Accessors;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IUserFootprintMapper extends BaseMapper<UserFootprint> {
    @Select("select * from t_user_footprint " +
            "where user_id=#{userId} and  exper_id=#{experId}")
    UserFootprint selectByUserExper(@Param("userId") String userId, @Param("experId") String experId);
}
