package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.ProgQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IProgQuestionMapper extends BaseMapper<ProgQuestion> {
    @Select("select id from t_prog_question where phase_id=#{phaseId} and number=#{number} " +
            "and group_id = #{groupId}")
    String selectIdByPhaseNumberGroup(@Param("phaseId") String phaseId,
                                      @Param("number") Integer number,
                                      @Param("groupId") String groupId);
}
