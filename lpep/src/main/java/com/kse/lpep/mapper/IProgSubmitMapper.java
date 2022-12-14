package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.ProgSubmit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface IProgSubmitMapper extends BaseMapper<ProgSubmit> {

    @Update("update `t_prog_submit` set `status`=#{status} where `id`=#{submitId}")
    boolean updateStatusById(@Param(value = "submitId") String submitId, @Param(value = "status") Integer status);

//    @Select("select * from `t_prog_submit` where `user_id`=#{userId} and `question_id`=#{questionId} " +
//            "order by `submit_time` desc limit ((#{row}-1) * #{size}), (#{row} * #{size})")
//    List<ProgSubmit> pageFindByUserIdAndProgQuestionIdOrderBySubmitTimeDesc(@Param(value = "userId") String userId,
//                                                                            @Param(value = "questionId") String questionId,
//                                                                            @Param(value = "row") Integer row,
//                                                                            @Param(value = "size") Integer size);

    @Select("<script>" +
            "<bind name='offset' value='(row-1)*size'/>" +
            "select * from `t_prog_submit` where `user_id`=#{userId} and `question_id`=#{questionId} " +
            "order by `submit_time` desc limit #{offset}, #{size}" +
            "</script>")
    List<ProgSubmit> pageFindByUserIdAndProgQuestionIdOrderBySubmitTimeDesc(@Param(value = "userId") String userId,
                                                                            @Param(value = "questionId") String questionId,
                                                                            @Param(value = "row") Integer row,
                                                                            @Param(value = "size") Integer size);

    @Select("select count(*) from t_prog_submit where user_id=#{userId} and question_id=#{questionId}")
    int countUserQuestionRecords(@Param(value = "userId") String userId,
                                 @Param(value = "questionId") String questionId);

    @Update("update `t_prog_submit` set `runner_output`=#{output}, `runner_time`=#{second} where `id`=#{id}")
    boolean updateRunnerOutputById(@Param(value = "id") String id,
                                   @Param(value = "output") String output,
                                   @Param(value = "second") Double second);


    @Select("<script>" +
            "select * from `t_prog_submit` where `user_id`=#{userId} and `question_id`=#{questionId} " +
            " and (`status` in  (" +
            "<foreach collection='unitId' separator=',' item='id'>" +
            "#{id} " +
            "</foreach> " +
            "))" + "</script>")
    List<ProgSubmit> selectByUserIdQuestionIdAndStatus(@Param(value = "userId") String userId,
                                                       @Param(value = "questionId") String questionId,
                                                        @Param("unitId") List<Integer> unitId);
}
