package com.kse.lpep.controller;

import com.kse.lpep.controller.vo.AddTesterToExperRequest;
import com.kse.lpep.controller.vo.BaseResponse;
import com.kse.lpep.controller.vo.CreateUserRequest;
import com.kse.lpep.service.IAdminService;
import com.kse.lpep.service.IExperService;
import com.kse.lpep.service.ITrainingMaterialService;
import com.kse.lpep.service.IUserService;
import com.kse.lpep.service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminorController {
    @Autowired
    private IAdminService adminService;
    @Autowired
    private IExperService experService;
    @Autowired
    private ITrainingMaterialService trainingMaterialService;
    @Autowired
    private IUserService userService;


    // 管理员分页查询所有实验
    @GetMapping("/listallexper")
    public BaseResponse<ExperInfoPage> listAllExper(int pageIndex, int pageSize){
        BaseResponse<ExperInfoPage> response = new BaseResponse<>();
        ExperInfoPage data = experService.getAllExper(pageIndex, pageSize);
        response.setStatus(200).setMsg("管理员查询所有实验数据").setData(data);
        return response;
    }

    // 情况1：该实验已经结束，数据传输错误
    // 情况2：正常情况
    @GetMapping("/startexper")
    public BaseResponse<Integer> startExper(String experId){
        BaseResponse<Integer> response = new BaseResponse<>();
        response.setStatus(200);
        try{
            int currentStatus = experService.queryExperCurrentStatus(experId);
            experService.modifyExperStatus(experId, currentStatus, 2);
            response.setMsg("管理员开始实验成功").setData(1);
        }catch (NullPointerException e){
            response.setMsg("管理员开始实验失败").setData(0);
        }finally {
            return response;
        }
    }

    @GetMapping("/endexper")
    public BaseResponse<Integer> endExper(String experId){
        BaseResponse<Integer> response = new BaseResponse<>();
        response.setStatus(200);
        try{
            int currentStatus = experService.queryExperCurrentStatus(experId);
            experService.modifyExperStatus(experId, currentStatus, 1);
            response.setMsg("管理员结束实验成功").setData(1);
        }catch (NullPointerException e){
            response.setMsg("管理员结束实验失败").setData(0);
        }finally {
            return response;
        }
    }

    // 实验分析和实验创建后面再说
//    @PostMapping("/createexper")
//    public BaseResponse<Integer> createExper(CreateExperRequest request){
//
//    }
    // 列举所有培训教材
    @GetMapping("listalltrainingmaterial")
    public BaseResponse<QueryTrainingMaterialInfoPage> listAllTrainingMaterial(int pageIndex, int pageSize){
        BaseResponse<QueryTrainingMaterialInfoPage> response = new BaseResponse<>();
        response.setStatus(200);
        try{
            QueryTrainingMaterialInfoPage data = trainingMaterialService.queryAllMaterialInfo(pageIndex, pageSize);
            response.setData(data).setMsg("管理员查询所有培训材料成功");
        }catch (NullPointerException e){
            response.setMsg("管理员查询所有培训材料失败");
        }finally {
            return response;
        }
    }

    // 删除指定的培训教材
    @GetMapping("deletetrainingmaterial")
    public BaseResponse<Integer> deleteTrainingMaterialById(String id){
        BaseResponse<Integer> response = new BaseResponse<>();
        response.setStatus(204);
        int status = trainingMaterialService.removeTrainingMaterialById(id);
        if(status == 1){
            response.setMsg("删除成功");
        }else{
            response.setMsg("删除失败").setStatus(214);
        }
        return response;
    }
    // 新增培训教材
    @PostMapping("createtrainingmaterial")
    public BaseResponse<String> createTrainingMaterial(){
        return null;
    }

    // 管理员分页查询tester
    @GetMapping("listalltester")
    public BaseResponse<TesterInfoPage> listAllTester(int pageIndex, int pageSize){
        BaseResponse<TesterInfoPage> response = new BaseResponse<>();
        response.setStatus(200).setMsg("列举所有参试人员的信息");
        TesterInfoPage data = userService.queryAllTester(pageIndex, pageSize);
        response.setData(data);
        return response;
    }


//    @GetMapping("listalltester")
//    public BaseResponse<List<TesterInfo>> listAllTester(){
//        BaseResponse<List<TesterInfo>> response = new BaseResponse<>();
//        response.setStatus(200).setMsg("列举所有参试人员的信息");
//        List<TesterInfo> data = userService.queryAllTester();
//        response.setData(data);
//        return response;
//    }

    // 管理员创建新用户
    @PostMapping("createuser")
    public BaseResponse<TesterInfo> createUser(@RequestBody CreateUserRequest request){
        BaseResponse<TesterInfo> response = new BaseResponse<>();
        response.setStatus(201);
        TesterInfo testerInfo = userService.createNewUser(request.getUsername(),
                request.getRealname(), request.getIsAdmin());
        if(testerInfo == null){
            response.setMsg("该用户已存在").setStatus(211);
        }else{
            response.setMsg("新建用户成功").setData(testerInfo);
        }
        return response;
    }

    // 管理员删除用户
    @GetMapping("deletetester")
    public BaseResponse<Integer> deleteTester(String userId){
        BaseResponse<Integer> response = new BaseResponse<>();
        response.setStatus(204).setMsg("删除成功");
        int data = userService.deleteUser(userId);
        if(data == 0){
            response.setMsg("删除失败");
        }
        response.setData(data);
        return response;
    }

    // 管理员添加用户到某个实验某个组
    @PostMapping("addtestertoexper")
    public BaseResponse<Integer> addTesterToExper(@RequestBody AddTesterToExperRequest request){
        BaseResponse<Integer> response = new BaseResponse<>();
        int data = adminService.addTesterToExper(request.getUserId(), request.getExperId(), request.getGroupId());
        response.setStatus(200).setMsg("添加成功");
        if(data == 0){
            response.setMsg("添加失败");
        }
        response.setData(data);
        return response;
    }

}