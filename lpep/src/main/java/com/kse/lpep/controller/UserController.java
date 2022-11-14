package com.kse.lpep.controller;

import com.kse.lpep.controller.vo.user.ExperToPartResponse;
import com.kse.lpep.controller.vo.user.TesterInfoResponse;
import com.kse.lpep.controller.vo.user.UserLoginRequest;
import com.kse.lpep.controller.vo.user.UserLoginResponse;
import com.kse.lpep.service.IUserService;
import com.kse.lpep.service.dto.ExperInfo;
import com.kse.lpep.service.dto.TesterInfo;
import com.kse.lpep.service.dto.UserLoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController  // json数据进行交互
@RequestMapping("user")
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public UserLoginResponse userLogin(UserLoginRequest request){
        String username = request.getUsername();
        String password = request.getPassword();
        UserLoginResult userLoginResult = userService.userLogin(username, password);
        int state = userLoginResult.getState();
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        if(state == 0){
            userLoginResponse.setSuccess(false).setMsg("用户名不存在");
            return userLoginResponse;
        }else if(state == 1){
            userLoginResponse.setSuccess(false).setMsg("用户账号密码错误");
            return userLoginResponse;
        }else{
            userLoginResponse.setSuccess(true).setMsg("用户登录成功").setId(userLoginResult.getId())
                    .setUserName(userLoginResult.getUserName()).setRealName(userLoginResult.getRealName())
                    .setIsAdmin(userLoginResult.getIsAdmin());
            return userLoginResponse;
        }
    }

    @GetMapping ("/testerinfo")
    public TesterInfoResponse testerInfo(String id){
        TesterInfo testerInfo = userService.testerBasicInfo(id);
        TesterInfoResponse testerInfoResponse = new TesterInfoResponse();
        testerInfoResponse.setUserName(testerInfo.getUserName()).setRealName(testerInfo.getRealName())
                .setCreateTime(testerInfo.getCreateTime()).setState(testerInfo.getState())
                .setMsg(testerInfo.getMsg());
        return testerInfoResponse;
    }

    /*
      用户查看待测试的实验
      用户可能存在实验中断的实验
     */
    @GetMapping("/expertopart")
    public ExperToPartResponse experToPart(String id){
        ExperToPartResponse experToPartResponse = new ExperToPartResponse();
        try{
            List<ExperInfo> experInfos = userService.experToParticipate(id);
            int state = experInfos.get(0).getState();
            // 情况2：存在中断的实验
            if(state == 0 || state == 2){
                experToPartResponse.setState(2).setMsg("用户存在中断的实验");
            }else{
                experToPartResponse.setState(1).setMsg("用户可以正常开始实验");
            }
            List<ExperInfo> list = experInfos.stream().collect(Collectors.toList());
            experToPartResponse.setExperInfos(list);
        }catch (NullPointerException e){
            // 情况0：用户不存在
            experToPartResponse.setState(0).setMsg("该用户不存在");
        }catch (IndexOutOfBoundsException e1){
            // 情况3：用户不存在待测试的实验
            experToPartResponse.setState(3).setMsg("用户没有待测试的实验");
        } finally {
            return experToPartResponse;
        }
    }
//    @GetMapping("/expertopart")
//    public ExperToPartResponse experToPart(String id){
//        List<ExperInfo> experInfos = userService.experToParticipate(id);
//        ExperToPartResponse experToPartResponse = new ExperToPartResponse();
//        if(experInfos.size() == 0){
//            // 用户不存在待测试的实验，返回空
//            experToPartResponse.setExperInfos(new ArrayList<ExperInfo>());
//            experToPartResponse.setState(1);
//            experToPartResponse.setMsg("用户没有待测试的实验");
//            return experToPartResponse;
//        }
//        int state = experInfos.get(0).getState();
//        List<ExperInfo> list = new ArrayList<>();
//        if(state == 0){
//            // 情况0：用户存在中断的实验
//            list.add(experInfos.get(0));
//            experToPartResponse.setExperInfos(list);
//            experToPartResponse.setState(0);
//            experToPartResponse.setMsg("用户存在中断的实验");
//        }else if(state == 1){
//            // 情况1：正常情况
//            for(ExperInfo experInfo : experInfos){
//                list.add(experInfo);
//            }
//            experToPartResponse.setExperInfos(list);
//            experToPartResponse.setState(1);
//            experToPartResponse.setMsg("正常实验");
//
//        }else if(state == 2){
//            // 情况2：用户不存在
//            experToPartResponse.setState(2);
//            experToPartResponse.setMsg("该用户不存在");
//        }
//        return experToPartResponse;
//    }

}
