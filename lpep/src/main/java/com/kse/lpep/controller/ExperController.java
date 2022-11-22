package com.kse.lpep.controller;


import com.kse.lpep.common.exception.ElementDuplicateException;
import com.kse.lpep.common.exception.FrontEndDataException;
import com.kse.lpep.common.exception.SaveFileIOException;
import com.kse.lpep.controller.vo.*;
import com.kse.lpep.service.IExperService;
import com.kse.lpep.service.IQuestionService;
import com.kse.lpep.service.dto.GroupInfo;
import com.kse.lpep.service.dto.NextPhaseStatusResult;
import com.kse.lpep.service.dto.NonProgQuestionInfo;
import com.kse.lpep.service.dto.ProgQuestionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("exper")
public class ExperController {
    @Autowired
    private IExperService experService;
    @Autowired
    private IQuestionService questionService;

    /*
    获取下一阶段问题类型，并判断实验是否结束
     */
    @PostMapping("/getnextphasestatus")
    public BaseResponse<NextPhaseStatusResult> getNextPhaseStatus(@RequestBody NextPhaseTypeRequest request){
        BaseResponse<NextPhaseStatusResult> response = new BaseResponse<>();
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
    public BaseResponse<List<NonProgQuestionInfo>> getNonProgQuestion(@RequestBody NonProgQuestionRequest request){
        BaseResponse<List<NonProgQuestionInfo>> response = new BaseResponse<>();
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
    public BaseResponse<ProgQuestionResult> getProgQuestion(@RequestBody ProgQuestionRequest request){
        BaseResponse<ProgQuestionResult> response = new BaseResponse<>();
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
    public BaseResponse<List<String>> listAllRunner(){
        BaseResponse<List<String>> response = new BaseResponse<>();
        List<String> data = experService.listRunnerType();
        response.setStatus(200).setMsg("返回所有runner的名称").setData(data);
        return response;
    }

    @GetMapping("/querygroups")
    public BaseResponse<List<GroupInfo>> queryGroups(String experId){
        BaseResponse<List<GroupInfo>> response = new BaseResponse<>();
        List<GroupInfo> data = experService.queryAllGroups(experId);
        response.setStatus(200).setData(data);
        return response;
    }

    /*
    获取caseid创建测试，需要传入测试顺序，第几个测试
    这个id需要存储在前端，后面一起给问题创建接口
     */
    @GetMapping("/getcaseid")
    public BaseResponse<String> getCaseId(Integer number){
        BaseResponse<String> response = new BaseResponse<>();
        String data = questionService.acquireCaseId(number);
        response.setStatus(200).setData(data);
        return response;
    }

    /*
    上传测试文件并返回文件名
     */
    @PostMapping("/uploadtestfile")
    public BaseResponse<String> uploadTestFile(
            @RequestParam(value = "isInput") Integer isInput,
            @RequestParam(value = "caseId") String caseId,
            @RequestParam(value = "experId") String experId,
            @RequestParam(value = "groupId") String groupId,
            @RequestParam(value = "file")MultipartFile file
            ){
        BaseResponse<String> response = new BaseResponse<>();
        try{
            String data = questionService.uploadExperTestFile(isInput, caseId, experId, groupId, file);
            response.setStatus(203).setMsg("上传成功").setData(data);
        }catch (FrontEndDataException | NullPointerException | SaveFileIOException e){
            response.setStatus(213).setMsg(e.getMessage());
        }
        return response;
    }

    @PostMapping("/nonprogsubmit")
    public BaseResponse<String> nonProgSubmit(@RequestBody NonProgSubmitRequest request){
        BaseResponse<String> response = new BaseResponse<>();
        try{
            String data = experService.submitNonProg(request.getUserId(), request.getAnswers());
            response.setStatus(205).setMsg("用户提交非编程问题成功").setData(data);
        }catch (ElementDuplicateException e) {
            response.setStatus(215).setMsg("用户提交非编程问题失败");
        }
        return response;
    }

}
