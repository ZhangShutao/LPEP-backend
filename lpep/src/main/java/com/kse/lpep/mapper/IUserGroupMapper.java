package com.kse.lpep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kse.lpep.mapper.pojo.UserGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IUserGroupMapper extends BaseMapper<UserGroup> {

    /**
     * 根据用户id和群组id查询唯一的用户群组记录
     * @param userId 用户id
     * @param groupId 群组id
     * @return 查询到的唯一记录
     */
    @Select("select * from t_user_group where user_id=#{userId} and group_id=#{groupId}")
    UserGroup getByUserIdAndGroupId(@Param("userId") String userId, @Param("groupId") String groupId);
}
