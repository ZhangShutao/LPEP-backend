package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.Group;
import com.kse.lpep.mapper.pojo.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IQuestionMapper extends BaseMapper<Question> {
    @Select("select * from `t_question` where `group_id`=#{groupId} and `phase_id`=#{phaseId}")
    List<Question> selectByGroupPhase(@Param(value = "groupId") String groupId, @Param(value = "phaseId") String phaseId);
}
