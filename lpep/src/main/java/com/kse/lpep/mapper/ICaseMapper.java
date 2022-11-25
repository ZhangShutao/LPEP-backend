package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.Case;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ICaseMapper extends BaseMapper<Case> {

    @Select("select * from `t_case` where `prog_question_id`=#{questionId} order by `number`")
    List<Case> selectByQuestionIdOrderedByNumber(@Param(value = "questionId") String questionId);
}
