package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.UserFootprint;
import lombok.experimental.Accessors;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserFootprintMapper extends BaseMapper<UserFootprint> {
}
