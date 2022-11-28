package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.User;
import com.kse.lpep.mapper.pojo.UserGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IUserGroupMapper extends BaseMapper<UserGroup> {

    @Select("select id, user_id, group_id, exper_id from t_user_group " +
            "where user_id=#{userId} ")
    List<UserGroup> selectByUserId(@Param("userId") String userId);



    @Select("select id, user_id, group_id, exper_id from t_user_group " +
            "where exper_id=#{experId} ")
    List<UserGroup> selectByExperId(@Param("experId") String experId);

    @Select("select id from t_user_group where exper_id=#{experId} and user_id=#{userId}")
    String selectIdByExperUser(@Param("experId") String experId, @Param("userId") String userId);


    /**
     * 根据用户id和群组id查询唯一的用户群组记录
     * @param userId 用户id
     * @param groupId 群组id
     * @return 查询到的唯一记录
     */
    @Select("select * from t_user_group where user_id=#{userId} and group_id=#{groupId}")
    UserGroup getByUserIdAndGroupId(@Param("userId") String userId, @Param("groupId") String groupId);

    @Select("select * from `t_user_group` where `user_id`=#{userId} and `exper_id`=#{experId}")
    List<UserGroup> getByUserIdAndExperId(@Param("userId") String userId, @Param("experId") String experId);

    @Select("select * from `t_user_group` where `user_id`=#{userId}")
    List<UserGroup> getByUserId(@Param("userId") String userId);


}
