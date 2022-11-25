package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.ProgSubmit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface IProgSubmitMapper extends BaseMapper<ProgSubmit> {

    @Update("update `t_prog_submit` set `status`=#{status} where `id`=#{submitId}")
    public boolean updateStatusById(@Param(value = "submitId") String submitId, @Param(value = "status") Integer status);
}
