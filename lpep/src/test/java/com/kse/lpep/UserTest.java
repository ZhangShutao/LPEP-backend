package com.kse.lpep;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.kse.lpep.mapper.IExperMapper;
import com.kse.lpep.mapper.IUserFootprintMapper;
import com.kse.lpep.mapper.IUserMapper;
import com.kse.lpep.mapper.pojo.Exper;
import com.kse.lpep.mapper.pojo.User;
import com.kse.lpep.mapper.pojo.UserFootprint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.object.UpdatableSqlQuery;

import java.util.*;

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

    @Test
    void testWrapper(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("realname", "曹宇");
        try{
            User user = userMapper.selectOne(queryWrapper);
            System.out.println(user);
        }catch (MybatisPlusException e){
            System.out.println("查询错误");
        }
    }

    @Autowired
    private IExperMapper experMapper;

    @Test
    void testSelectById(){
        String id = "34f6879f61c911edab7a2cf05decb14f";
        String title = "ASP测试";
        QueryWrapper<Exper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Exper exper = experMapper.selectOne(queryWrapper);
        System.out.println(exper);
    }

    // 测试查询单个数据不存在时抛出的异常是什么
    @Test
    void testExption(){
//        1.使用主键查询测试，结果为null
//        String uid = "0105b5f4625011edab7a2cf05decb14g";
//        User user = userMapper.selectById(uid);

//        2.使用selectone测试，结果还是null
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("username", "zhangsan");
//        User user = userMapper.selectOne(queryWrapper);
//        System.out.println(user);

//        3.使用try抓获null，不存在时抓取成功
        try{
            String uid = "0105b5f4625011edab7a2cf05decb14f";
            User user = userMapper.selectById(uid);
            System.out.println(user.getRealname());
        }catch (NullPointerException e){
            System.out.println("该用户不存在");
        }
    }

    @Autowired
    IUserFootprintMapper userFootprintMapper;
    @Test
    void testUserFootprint(){
        String id = "f6c729d3624f11edab7a2cf05decb14f";
        String experId = "34f6879f61c911edab7a2cf05decb14f";
        QueryWrapper<UserFootprint> queryWrapper1 = new QueryWrapper<>();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("user_id", id);
        paramsMap.put("exper_id", experId);
        queryWrapper1.allEq(paramsMap);
//            queryWrapper1.eq("user_id", id).eq("exper_id", experId);
        UserFootprint userFootprint = userFootprintMapper.selectOne(queryWrapper1);
        if(userFootprint == null){
            System.out.println("null");
        }else {
            System.out.println("balnk");
        }
    }

    // 测试结果为从小到大排序
    @Test
    void testSort(){
        List<Integer> list = new ArrayList<>();
        list.add(7); list.add(4); list.add(8); list.add(1);
        Collections.sort(list, (a, b) ->  a - b);
        for(int x : list){
            System.out.println(x);
        }
    }

    // 测试mybatisPlus的插入和修改功能
    @Test
    void testInsertAndUpdate(){
        UpdateWrapper<UserFootprint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", "f6c729d3624f11edab7a2cf05decb14f")
                .eq("exper_id", "34f6879f61c911edab7a2cf05decb14f");
        UserFootprint userFootprint = new UserFootprint();
//        userFootprint.setCurrentPhaseId("100");
//        userFootprint.setCurrentQuestionId("111");
        userFootprintMapper.update(userFootprint, updateWrapper);

    }

}
