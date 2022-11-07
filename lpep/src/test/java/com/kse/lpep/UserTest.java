package com.kse.lpep;

import com.kse.lpep.mapper.IUserMapper;
import com.kse.lpep.mapper.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class UserTest {
    @Autowired
    private IUserMapper userMapper;

    // 测试查询全部数据
    @Test
    void testSelectList() {
        List<User> users = userMapper.selectList(null);
        for(User user : users){
            System.out.println(user);
        }
    }

    // 测试插入数据
    @Test
    void testInsert() {
        User user = new User();
        user.setUsername("5224");
        user.setPassword("5224");
        user.setRealname("曹宇");
        user.setIsAdmin(0);
        userMapper.insert(user);
    }

    // 测试通过用户username获取主键并删除数据
    @Test
    void testDelete() {
        String id = "c615ba5a5e9811ed9e6e2cf05decb14f";
        userMapper.deleteById(id);
    }

}
