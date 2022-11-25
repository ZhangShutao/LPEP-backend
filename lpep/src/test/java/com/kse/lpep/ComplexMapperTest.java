package com.kse.lpep;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kse.lpep.mapper.IGroupMapper;
import com.kse.lpep.mapper.IUserGroupMapper;
import com.kse.lpep.mapper.IUserMapper;
import com.kse.lpep.mapper.pojo.Group;
import com.kse.lpep.mapper.pojo.User;
import com.kse.lpep.mapper.pojo.UserGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 张舒韬
 * @since 2022/11/24
 */
@SpringBootTest
public class ComplexMapperTest {

    @Autowired
    IUserGroupMapper userGroupMapper;

    @Autowired
    IUserMapper userMapper;

    @Autowired
    IGroupMapper groupMapper;

    @Test
    @Rollback
    public void testDoubleCondition() {
        List<User> users = userMapper.selectList(new QueryWrapper<>());
        List<Group> groups = groupMapper.selectList(new QueryWrapper<>());
        User user = users.get(0);
        Group group = groups.get(0);

        if (userGroupMapper.getByUserIdAndGroupId(user.getId(), group.getId()) != null) {
            userGroupMapper.insert(new UserGroup(user.getId(), group.getId(), group.getExperId()));
        }

        assert(userGroupMapper.getByUserIdAndGroupId(user.getId(), group.getId()) != null);
    }
}
