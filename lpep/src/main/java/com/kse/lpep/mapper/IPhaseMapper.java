package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.Phase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IPhaseMapper extends BaseMapper<Phase> {
    // 这个地方可以用int吗
    @Select("select id, name, exper_id, phase_number, type from t_phase " +
            "where exper_id=#{experId} and phase_number=#{phaseNumber}")
    Phase selectByExperAndNumber(@Param("experId") String experId, @Param("phaseNumber") Integer phaseNumber);
}
