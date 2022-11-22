package com.kse.lpep.controller;

import com.kse.lpep.common.exception.SaveFileIOException;
import com.kse.lpep.common.utility.SavingFile;
import com.kse.lpep.controller.vo.BaseResponse;
import com.kse.lpep.controller.vo.UserLoginRequest;
import com.kse.lpep.service.ITrainingMaterialService;
import com.kse.lpep.service.IUserService;
import com.kse.lpep.service.dto.ExperInfo;
import com.kse.lpep.service.dto.PersonalResult;
import com.kse.lpep.service.dto.TrainingMaterialInfo;
import com.kse.lpep.service.dto.UserLoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;


@RestController  // json数据进行交互
@RequestMapping("user")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private ITrainingMaterialService trainingMaterialService;

    @PostMapping("/login")
    public BaseResponse<UserLoginResult> userLogin(@RequestBody UserLoginRequest request){
        // 使用该注解后，前端的json格式request请求才可以正常运行，但是postman中的from-data不行
        // postman使用raw的json格式可以访问，只有post请求才使用该注解
        BaseResponse<UserLoginResult> response = new BaseResponse<>();
        try{
            UserLoginResult userLoginResult = userService.userLogin(request.getUsername(),
                    request.getPassword());
            response.setStatus(200).setMsg("登录成功").setData(userLoginResult);
        }catch (NullPointerException e){
            response.setStatus(200).setMsg("登录错误");
        }
        return response;
    }
    @GetMapping ("/getpersonalinfo")
    public BaseResponse<PersonalResult> getPersonalInfo(String userId){
        BaseResponse<PersonalResult> response = new BaseResponse<>();
        try{
            PersonalResult personalResult = userService.personalBasicInfo(userId);
            response.setStatus(200).setMsg("访问个人数据成功").setData(personalResult);
        }catch (NullPointerException e){
            response.setStatus(200).setMsg("访问个人数据失败");
        }
        return response;

    }

    /*
      用户查看待测试的实验
      用户可能存在实验中断的实验
     */
    @GetMapping("/experstopart")
    public BaseResponse<List<ExperInfo>> expersToPart(String userId){
        BaseResponse<List<ExperInfo>> response = new BaseResponse<>();
        try{
            List<ExperInfo> experInfos = userService.expersToParticipate(userId);
            int state = experInfos.get(0).getState();
            // 情况2：存在中断的实验
            if(state == 0 || state == 2){
                response.setStatus(200).setMsg("用户存在中断的实验");
            }else{
                response.setStatus(200).setMsg("用户可以正常开始实验");
            }
            response.setData(experInfos);
        }catch (NullPointerException e){
            // 情况0：用户不存在
            response.setStatus(200).setMsg("该用户不存在");
        }catch (IndexOutOfBoundsException e1){
            // 情况3：用户不存在待测试的实验，考虑要不要和正常情况合并
            response.setStatus(200).setMsg("用户没有待测试的实验");
        }
        return response;
    }
    @GetMapping("/querypersonaltrainingmaterial")
    public BaseResponse<List<TrainingMaterialInfo>> listPersonalTrainingMaterial(String userId){
        BaseResponse<List<TrainingMaterialInfo>> response = new BaseResponse<>();
        response.setStatus(200);
        try{
            List<TrainingMaterialInfo> data = trainingMaterialService.listPersonalTextbook(userId);
            response.setData(data).setMsg("已返回个人实验培训材料");
        }catch (NullPointerException e){
            response.setMsg("数据传入错误");
        }
        return response;
    }

    @PostMapping("/testfile")
    public BaseResponse<String> testFile(
            @RequestParam(value = "file") MultipartFile file
            ){
        BaseResponse<String> resp = new BaseResponse<>();
        String saveName = "1.txt";
        String savePath = "c:/test";
        try {
            SavingFile.saveFile(file, saveName, savePath);
            resp.setStatus(203);
        }catch (NullPointerException | SaveFileIOException e){
            resp.setStatus(213).setMsg(e.getMessage());
        }
        return resp;
    }
}
