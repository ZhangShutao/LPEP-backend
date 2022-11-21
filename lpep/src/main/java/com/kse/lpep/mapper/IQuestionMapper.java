package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IQuestionMapper extends BaseMapper<Question> {
}
