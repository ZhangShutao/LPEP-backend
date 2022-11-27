package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.Case;
import com.kse.lpep.mapper.pojo.Submit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ISubmitMapper extends BaseMapper<Submit> {
    @Select("select * from `t_submit` where `user_id`=#{userId} and `question_id`=#{questionId}")
    List<Submit> selectByUserQuestion(@Param(value = "userId") String userId,
                                      @Param(value = "questionId") String questionId);
}
