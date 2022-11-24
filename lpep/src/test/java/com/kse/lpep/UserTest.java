package com.kse.lpep;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kse.lpep.mapper.*;
import com.kse.lpep.mapper.pojo.*;
import com.kse.lpep.service.dto.QueryTrainingMaterialInfo;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.object.UpdatableSqlQuery;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

    // 测试delete
    @Test
    void testMyDelete(){
        int x = userMapper.deleteById("e2a51e8967f411ed8ed92cf05decb14f");
        System.out.println(x);
    }

    // 测试insert相同账号的用户
    @Test
    void testInsertSameUser(){
        User user = new User();
        String s = "21312";
        user.setUsername(s).setPassword(s).setRealname(s);
        user.setIsAdmin(0);
        try {
            int status = userMapper.insert(user);
            System.out.println(status);
        }catch (DuplicateKeyException e){
            System.out.println("捕获异常");
        }
    }

    // 测试删除不存在的用户
    // 测试结果：成功删除1，删除失败0，没有异常
    @Test
    void testDeleteUser(){
        String userId = "526997e2689611ed8ed92cf05decb14f";
        int status = userMapper.deleteById(userId);
        System.out.println(status);
    }


    // 测试mybatisPlus插入成功后返回主键
//    @Test
//    void testReturnId(){
//        User user = new User();
//        user.setUsername("111").setRealname("111").setPassword("123").setIsAdmin(0);
//        userMapper.insert(user);
//        String s = user.getId();
//        System.out.println(s);
//    }

    // 测试mybatisPlus的分页功能
    @Test
    void testPage(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_admin", 0);
//        // 分页用法1
//        Page<User> userPage = new Page<>(1, 2, true);
//        IPage<User> userIPage = userMapper.selectPage(userPage, queryWrapper);
//        System.out.println("总页数" + userPage.getPages());
//        System.out.println("总记录数" + userIPage.getTotal());
//        userPage.getRecords().forEach(System.out::println);


        // 分页用法2
        Page<Map<String, Object>> mapPage = new Page<>(1, 2, true);
        IPage<Map<String, Object>> mapIPage = userMapper.selectMapsPage(mapPage, queryWrapper);
        System.out.println("总页数" + mapIPage.getPages());
        System.out.println("总记录数" + mapIPage.getTotal());
        mapIPage.getRecords().forEach(System.out::println);
    }


    @Autowired
    private ICaseMapper caseMapper;
    // 测试插入数据后原数据状态
    // 测试结果，使用mp自带的方式生成uuid可以拿到主键，删除数据库中的uuid生成
    @Test
    void testInsertStatus(){
        Case myCase = new Case();
        myCase.setNumber(1);
        int x = caseMapper.insert(myCase);
        System.out.println(x);
        System.out.println(myCase.getId());
    }



    // 测试时间转换
    @Test
    void testTime(){
        String startTime = "1999-08-12 15:35:54";
        Timestamp myStartTime = Timestamp.valueOf(startTime);
        System.out.println(myStartTime);
    }

    @Test
    void testUpdate(){
        Case myCase = new Case();
        myCase.setId("55af1db609b36be80ad8c455fffc8b0").setProgQuestionId("171");
        caseMapper.updateById(myCase);
    }

    @Test
    void testStringUtil(){
        String a = "asd";
        String b = "asd";
        System.out.println(StringUtils.equals(a, b));
    }

    // 测试查询条件中加入不等于某个结合这个条件
    // 测试成功，需要合理的使用and和or的连接
    // notin需要size不为0
    @Test
    void testNotIn(){
        List<String> experIds = new ArrayList<>();
        experIds.add("34f6879f61c911edab7a2cf05decb14f");
        experIds.add("a749bcfc6a0b11ed8ed92cf05decb14f");

        LambdaQueryWrapper<Exper> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.and(wp->wp.eq(Exper::getState, 0).notIn(Exper::getId, experIds))
                .or(wp->wp.eq(Exper::getState, 2).notIn(Exper::getId, experIds));


        List<Exper> expers = experMapper.selectList(lambdaQueryWrapper);
        expers.stream().forEach(System.out::println);
    }

    // 测试加密
    // 测试成功
    @Test
    void testEncode(){
        String s1 = "12345qw";
        String s2 = "214defwdaw";
        String s3 = "12345qw";
        String encode1 = DigestUtil.sha256Hex(s1);
        String encode2 = DigestUtil.sha256Hex(s2);
        String encode3 = DigestUtil.sha256Hex(s3);
        System.out.println(encode1);
        System.out.println(encode2);
        System.out.println(encode3);
    }

    // 路径测试
    // 测试结果：mkdir只会创建一层目录，上层目录不存在则创建失败，mkdirs才是正解
    @Test
    void testSpace(){
        String fileSeparator = FileSystems.getDefault().getSeparator();
        String dirname = "c://tmp/sb";
        File d = new File(dirname);
        d.mkdirs();
    }

    @Autowired
    private IUserGroupMapper userGroupMapper;
    // mapper层写select方法的测试
    @Test
    void testMySelect(){
//        User user = userMapper.selectAccountPassword("22222", "22222");
//        System.out.println(user.getRealname());
        userGroupMapper.selectByUserId("2045747668c211ed8ed92cf05decb14f").forEach(System.out::println);

    }


}
