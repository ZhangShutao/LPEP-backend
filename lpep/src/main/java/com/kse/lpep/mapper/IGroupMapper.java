package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.Group;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IGroupMapper extends BaseMapper<Group> {

    @Select("select * from `t_group` where `exper_id`=#{experId}")
    public List<Group> selectByExperId(@Param(value = "experId") String experId);
}
