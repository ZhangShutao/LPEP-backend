package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.TrainingMaterial;
import com.kse.lpep.mapper.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ITrainingMaterialMapper extends BaseMapper<TrainingMaterial> {
    @Select("select id, group_id, title, absolute_path, last_update_time from t_training_material" +
            " where group_id=#{groupId}")
    TrainingMaterial selectByGroup(@Param("groupId") String groupId);

    @Select("select id, group_id, title, absolute_path, last_update_time from t_training_material" +
            " where absolute_path=#{savePath}")
    TrainingMaterial selectByPath(@Param("savePath") String savePath);
}
