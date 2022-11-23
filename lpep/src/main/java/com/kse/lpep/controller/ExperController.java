package com.kse.lpep.controller;


import com.kse.lpep.controller.vo.BaseResponse;
import com.kse.lpep.controller.vo.NonProgQuestionRequest;
import com.kse.lpep.controller.vo.NextPhaseTypeRequest;
import com.kse.lpep.controller.vo.ProgQuestionRequest;
import com.kse.lpep.service.IExperService;
import com.kse.lpep.service.dto.NextPhaseStatusResult;
import com.kse.lpep.service.dto.NonProgQuestionInfo;
import com.kse.lpep.service.dto.ProgQuestionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("exper")
public class ExperController {
    @Autowired
    private IExperService experService;

    /*
    获取下一阶段问题类型，并判断实验是否结束
     */
    @PostMapping("/getnextphasestatus")
    public BaseResponse getNextPhaseStatus(@RequestBody NextPhaseTypeRequest request){
        BaseResponse response = new BaseResponse();
        try{
            NextPhaseStatusResult nextPhaseStatusResult = experService.acquirePhaseStatus(
                    request.getUserId(), request.getExperId(), request.getPhaseNumber());
            if(nextPhaseStatusResult.getIsEnd() == 1){
                response.setMsg("实验结束");
            }else if(nextPhaseStatusResult.getIsProg() == 1){
                response.setMsg("该实验的下一个阶段题目类型为编程题");
            }else{
                response.setMsg("该实验的下一个阶段题目类型为非编程题");
            }
            response.setData(nextPhaseStatusResult);
        }catch (NullPointerException e){
            response.setMsg("数据传递出错");
        }finally {
            response.setStatus(200);
            return response;
        }
    }

    /*
    用户获取下一阶段的非编程题目
    情况0：前端传递数据错误
    情况1：正常获取题目，一次性获取所有题目
    步骤：1）获取题目，处理异常
         2）正常获取题目则修改用户做题状态
         3）包装返回
     */
    @PostMapping("/getnonprogquestion")
    public BaseResponse getNonProgQuestion(@RequestBody NonProgQuestionRequest request){
        BaseResponse response = new BaseResponse();
        // 1）获取题目并处理异常
        try{
            List<NonProgQuestionInfo> nonProgQuestionInfos = experService
                    .acquireNonProgQuestion(request.getUserId(),
                            request.getExperId(), request.getPhaseNumber());
            // 正常回应，返回该阶段题目
            response.setStatus(200).setMsg("已返回指定阶段题目")
                    .setData(nonProgQuestionInfos);
        }catch (NullPointerException e){
            // 前端输入异常，状态置数0
            response.setStatus(200).setMsg("前端请求出错");
        }finally {
            return response;
        }
    }
    @PostMapping("/getprogquestion")
    public BaseResponse getProgQuestion(@RequestBody ProgQuestionRequest request){
        BaseResponse response = new BaseResponse();
        // 1）获取题目并处理异常
        try{
            ProgQuestionResult progQuestionResult = experService
                    .acquireProgQuestion(request.getUserId(), request.getExperId(),
                            request.getPhaseNumber(), request.getQuestionNumber());
            // 正常回应，返回该阶段题目
            response.setStatus(200).setMsg("已返回指定题目")
                    .setData(progQuestionResult);
        }catch (NullPointerException e){
            // 前端输入异常，状态置数0
            response.setStatus(200).setMsg("前端请求出错");
        }finally {
            return response;
        }
    }

    @GetMapping("listallrunner")
    public BaseResponse listAllRunner(){
        BaseResponse response = new BaseResponse();
        List<String> data = experService.listRunnerType();
        response.setStatus(200).setMsg("返回所有runner的名称").setData(data);
        return response;
    }



}
