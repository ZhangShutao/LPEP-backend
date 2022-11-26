package com.kse.lpep.controller;

import com.kse.lpep.common.constant.ConstantCode;
import com.kse.lpep.common.exception.DeleteException;
import com.kse.lpep.common.exception.RecordNotExistException;
import com.kse.lpep.common.exception.SaveFileIOException;
import com.kse.lpep.common.exception.UserLoginException;
import com.kse.lpep.common.utility.SavingFile;
import com.kse.lpep.common.utility.ValidUtil;
import com.kse.lpep.controller.vo.BaseResponse;
import com.kse.lpep.controller.vo.DeleteFromExperRequest;
import com.kse.lpep.controller.vo.ExperGetUserPageRequest;
import com.kse.lpep.controller.vo.UserLoginRequest;
import com.kse.lpep.service.ITrainingMaterialService;
import com.kse.lpep.service.IUserService;
import com.kse.lpep.service.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController  // json数据进行交互
@RequestMapping("user")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private ITrainingMaterialService trainingMaterialService;

    /**
     * 用户登录接口
     * 功能介绍：用户输入账号密码进行登录，密码使用sha256算法加密（数据库中密码已加密）并验证
     * 登录成功200，失败210，数据校验错误300
     * @param request username(用户账号)；password(账号密码)
     * @param bindingResult
     * @return
     * @see UserLoginResult
     */
    @PostMapping("/login")
    public BaseResponse userLogin(@RequestBody @Valid UserLoginRequest request,
                                                   BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        // 1.处理数据校验异常
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        // 2.处理用户登录并返回
        try{
            UserLoginResult data = userService.userLogin(request.getUsername(),
                    request.getPassword());
            response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("登录成功").setData(data);
        }catch (UserLoginException e){
            response.setStatus(ConstantCode.QUERY_FAIL).setMsg(e.getMessage());
        }
        return response;
    }

    /**
     * 用户查看个人信息接口
     * 功能介绍：管理员查看自己的身份信息
     * 成功200，失败210，数据校验失败300
     * @param userId
     * @return
     * @See PersonalResult
     */
    @GetMapping ("/getpersonalinfo")
    public BaseResponse getPersonalInfo(String userId){
        BaseResponse response = new BaseResponse();
        if(StringUtils.isBlank(userId)){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg("用户id为空");
            return response;
        }
        try{
            PersonalResult data = userService.personalBasicInfo(userId);
            response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("访问个人数据成功").setData(data);
        }catch (NullPointerException e){
            response.setStatus(ConstantCode.QUERY_FAIL).setMsg("访问个人数据失败");
        }
        return response;
    }


    /**
     * 用户查看待进行的实验接口
     * 功能介绍：用户查看自己还需要进行的实验，后续开始实验；或者查看自己中断的实验
     * 成功200，失败210，数据校验错误300
     * @param userId
     * @return
     * @See List<ExperInfo>
     */
    @GetMapping("/experstopart")
    public BaseResponse expersToPart(String userId){
        BaseResponse response = new BaseResponse();
        if(StringUtils.isBlank(userId)){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg("用户id为空");
            return response;
        }
        try{
            List<ExperInfo> data = userService.expersToParticipate(userId);
            int state = data.get(0).getState();
            if(state == 0 || state == 2){
                // 存在中断的实验
                response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("用户存在中断的实验");
            }else{
                // 正常情况
                response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("用户正常开始实验");
            }
            response.setData(data);
        }catch (NullPointerException e){
            // 用户不存在
            response.setStatus(ConstantCode.QUERY_FAIL).setMsg("该用户不存在");
        }catch (IndexOutOfBoundsException e1){
            // 用户不存在待测试的实验
            response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("用户没有待测试的实验");
        }
        return response;
    }

    /**
     * 用户查看个人相关培训材料接口
     * 功能介绍：返回用户相关实验的培训材料列表
     * 成功200，失败210，数据校验错误300
     * @param userId
     * @return
     * @See List<TrainingMaterialInfo>
     */
    @GetMapping("/querypersonaltrainingmaterial")
    public BaseResponse listPersonalTrainingMaterial(String userId){
        BaseResponse response = new BaseResponse();
        if(StringUtils.isBlank(userId)){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg("用户id为空");
            return response;
        }
        try{
            List<TrainingMaterialInfo> data = trainingMaterialService.listPersonalTextbook(userId);
            response.setData(data).setMsg("返回个人实验培训材料").setStatus(ConstantCode.QUERY_SUCCESS);
        }catch (NullPointerException e){
            response.setMsg(e.getMessage()).setStatus(ConstantCode.QUERY_FAIL);
        }
        return response;
    }


    /**
     * 根据用户账号获取用户真实姓名和id接口
     * 成功200；失败210；校验错误300
     * @param username
     * @return UserRealnameDto
     */
    @GetMapping("getrealname")
    public BaseResponse getUserRealname(String username){
        BaseResponse response = new BaseResponse();
        if(StringUtils.isBlank(username)){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg("用户id为空");
            return response;
        }
        try{
            UserRealnameDto data = userService.getRealname(username);
            response.setData(data).setStatus(ConstantCode.QUERY_SUCCESS);
        }catch (NullPointerException e){
            response.setStatus(ConstantCode.QUERY_FAIL).setMsg(e.getMessage());
        }
        return response;
    }

    /**
     * 管理员获取指定实验的所有用户和用户分组接口
     * 成功200；失败210；校验失败300
     * @return
     */
    @PostMapping("getallusergroup")
    public BaseResponse getAllUserGroup(@RequestBody @Valid ExperGetUserPageRequest request,
                                        BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        // 1.处理数据校验异常
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        UserWithGroupInfoPage data = new UserWithGroupInfoPage();
        data.setRecordCount(0);
        try{
            data = userService.getAllUserGroupByExperId(request.getExperId(),
                    request.getPageIndex(), request.getPageSize());
            response.setStatus(ConstantCode.QUERY_SUCCESS).setData(data);
        }catch (NullPointerException e){
            data.setUserWithGroupInfoList(new ArrayList<>());
            response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg(e.getMessage()).setData(data);
        }catch (RecordNotExistException e1){
            response.setStatus(ConstantCode.QUERY_FAIL).setMsg(e1.getMessage());
        }
        return response;
    }

    /**
     * 管理员从指定实验移除用户接口
     * 成功204；失败214；校验失败300；
     * @param request
     * @param bindingResult
     * @return
     */
    @PostMapping("deletefromexper")
    public BaseResponse deleteFromExper(@RequestBody @Valid DeleteFromExperRequest request,
                                        BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        // 1.处理数据校验异常
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        // 删除用户
        try{
            userService.deleteUserFromExper(request.getExperId(), request.getUserId());
            response.setStatus(ConstantCode.DELETE_SUCCESS).setMsg("删除成功");
        }catch (RecordNotExistException | DeleteException e){
            response.setStatus(ConstantCode.DELETE_FAIL).setMsg(e.getMessage());
        }
        return response;
    }

    @PostMapping("mytestupload")
    public BaseResponse testUploadFile(@RequestParam(value = "file") MultipartFile file){
        String fileSeparator = FileSystems.getDefault().getSeparator();
        String targetPath = fileSeparator + "mytesta";
        String saveName = "saveName.txt";
        SavingFile.saveFile(file, saveName, targetPath);
        return null;
    }
}
