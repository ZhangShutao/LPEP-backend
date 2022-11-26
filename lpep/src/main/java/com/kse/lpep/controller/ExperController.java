package com.kse.lpep.controller;


import com.kse.lpep.common.constant.ConstantCode;
import com.kse.lpep.common.exception.*;
import com.kse.lpep.common.utility.ValidUtil;
import com.kse.lpep.controller.vo.*;
import com.kse.lpep.service.IExperService;
import com.kse.lpep.service.IQuestionService;
import com.kse.lpep.service.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("exper")
public class ExperController {
    @Autowired
    private IExperService experService;
    @Autowired
    private IQuestionService questionService;

    /**
     * 获取下一阶段问题类型接口
     * 功能介绍：前端询问下一阶段是什么类型问题
     * 访问成功200；失败210；数据校验失败300
     * @param request userId（用户id）；experId（实验id）；phaseNumber（实验的第几阶段）
     * @return
     * @See NextPhaseStatusResult
     */
    @PostMapping("/getnextphasestatus")
    public BaseResponse getNextPhaseStatus(@RequestBody @Valid NextPhaseTypeRequest request,
                                           BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        // 1.处理数据校验异常
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        try{
            NextPhaseStatusResult nextPhaseStatusResult = experService.acquirePhaseStatus(
                    request.getUserId(), request.getExperId(), request.getPhaseNumber());
            response.setStatus(ConstantCode.QUERY_SUCCESS);
            if(nextPhaseStatusResult.getIsEnd() == 1){
                response.setMsg("实验结束");
            }else if(nextPhaseStatusResult.getIsProg() == 1){
                response.setMsg("该实验的下一个阶段题目类型为编程题");
            }else{
                response.setMsg("该实验的下一个阶段题目类型为非编程题");
            }
            response.setData(nextPhaseStatusResult);
        }catch (NullPointerException e){
            response.setMsg("数据传递出错").setStatus(ConstantCode.QUERY_FAIL);
        }
        return response;
    }

    /**
     * 用户获取下一阶段非编程题目接口
     * 功能介绍：前端确定下阶段为非编程题，然后调用该接口获取题目
     * 获取成功200；问题不存在206；失败210；校验错误300
     * @param request
     * @param bindingResult
     * @return List<NonProgQuestionInfo>
     */
    @PostMapping("/getnonprogquestion")
    public BaseResponse getNonProgQuestion(@RequestBody @Valid NonProgQuestionRequest request,
                                           BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        // 1.处理数据校验异常
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        // 2.获取题目
        try{
            List<NonProgQuestionInfo> nonProgQuestionInfos = experService
                    .acquireNonProgQuestion(request.getUserId(),
                            request.getExperId(), request.getPhaseNumber());
            // 正常回应，返回该阶段题目
            response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("已返回指定阶段题目")
                    .setData(nonProgQuestionInfos);
        }catch (NullPointerException e){
            // 前端输入异常，状态置数0
            response.setStatus(ConstantCode.QUERY_FAIL).setMsg("前端请求出错");
        }catch (QuestionNotExistException e1){
            response.setStatus(ConstantCode.EMPTY_QUESTION).setMsg(e1.getMessage());
        }
        return response;
    }

    /**
     * 用户获取下一阶段编程题目接口
     * 功能介绍：前端确定下阶段为编程题，然后调用该接口获取题目
     * 获取成功200；问题不存在206；失败210；校验错误300
     * @param request
     * @return ProgQuestionResult
     */
    @PostMapping("/getprogquestion")
    public BaseResponse getProgQuestion(@RequestBody @Valid ProgQuestionRequest request,
                                        BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        // 1.处理数据校验异常
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        // 2.获取题目并处理异常
        try{
            ProgQuestionResult progQuestionResult = experService
                    .acquireProgQuestion(request.getUserId(), request.getExperId(),
                            request.getPhaseNumber(), request.getQuestionNumber());
            // 正常回应，返回该阶段题目
            response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("已返回指定题目")
                    .setData(progQuestionResult);
        }catch (NullPointerException | RecordNotExistException | ElementDuplicateException e){
            // 前端输入异常，状态置数0
            response.setStatus(ConstantCode.QUERY_FAIL).setMsg(e.getMessage());
        }catch (QuestionNotExistException e1){
            response.setStatus(ConstantCode.EMPTY_QUESTION).setMsg(e1.getMessage());
        }
        return response;
    }

    /**
     * 列举全部求解器接口
     * 功能介绍：管理员创建实验时需要列举全部求解器并选择一个
     * 获取成功200；我这里没有失败
     * @return List<RunnerInfo>
     */
    @GetMapping("listallrunner")
    public BaseResponse listAllRunner(){
        BaseResponse response = new BaseResponse();
        List<RunnerInfo> data = experService.listRunnerType();
        response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("返回runner").setData(data);
        return response;
    }

    /**
     * 根据实验id查询组别接口
     * 功能介绍：给定实验id，然后查看该id下所有的组别接口
     * 获取成功200；失败210；校验失败300
     * @param experId
     * @return List<GroupInfo>
     */
    @GetMapping("/querygroups")
    public BaseResponse queryGroups(String experId){
        BaseResponse response = new BaseResponse();
        if(StringUtils.isBlank(experId)){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg("实验id不能为空");
            return response;
        }
        try{
            List<GroupInfo> data = experService.queryAllGroups(experId);
            response.setStatus(ConstantCode.QUERY_SUCCESS).setData(data).setMsg("成功返回组别");
        }catch (NullPointerException e){
            response.setStatus(ConstantCode.QUERY_FAIL).setMsg(e.getMessage());
        }
        return response;
    }


    /**
     * 获取caseId接口
     * 功能介绍：管理员上传test文件时，需要先生成caseId
     * 成功200；没有失败；校验失败300
     * 这里存在一个顺序校验的问题（目前选择相信前端）
     * @return String
     */
    @GetMapping("/getcaseid")
    public BaseResponse getCaseId(){
        BaseResponse response = new BaseResponse();
        String data = questionService.acquireCaseId();
        response.setStatus(ConstantCode.QUERY_SUCCESS).setData(data);
        return response;
    }

    /*
    上传测试文件并返回文件名
     */

    /**
     * 管理员上传编程题测试文件接口
     * 成功203；失败213；校验失败300；
     * @param isInput
     * @param caseId
     * @param experId
     * @param groupId
     * @param file
     * @return String，返回了文件名
     */
    @PostMapping("/uploadtestfile")
    public BaseResponse uploadTestFile(
            @RequestParam(value = "isInput") Integer isInput,
            @RequestParam(value = "caseId") String caseId,
            @RequestParam(value = "experId") String experId,
            @RequestParam(value = "groupId") String groupId,
            @RequestParam(value = "file")MultipartFile file
            ){
        BaseResponse response = new BaseResponse();
        // 1.数据校验
        try{
            questionService.checkParam(isInput, caseId, experId, groupId);
        }catch (ParamException e){
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(e.getMessage());
        }
        // 2.上传文件
        try{
            String data = questionService.uploadExperTestFile(isInput, caseId, experId, groupId, file);
            response.setStatus(ConstantCode.UPLOAD_SUCCESS).setMsg("上传成功").setData(data);
        }catch (FrontEndDataException | NullPointerException | SaveFileIOException e){
            response.setStatus(ConstantCode.UPLOAD_FAIL).setMsg(e.getMessage());
        }
        return response;
    }

    /**
     * 用户非编程题提交接口
     * 功能介绍：用户提交一个非编程阶段的答案
     * 成功203；失败213；校验失败300
     * @param request
     * @param bindingResult
     * @return null
     */
    @Transactional
    @PostMapping("/nonprogsubmit")
    public BaseResponse nonProgSubmit(@RequestBody @Valid NonProgSubmitRequest request,
                                      BindingResult bindingResult){
        BaseResponse response = new BaseResponse();
        // 1.处理数据校验异常
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(errorMessage);
            return response;
        }
        try{
            experService.submitNonProg(request.getUserId(), request.getExperId(), request.getPhaseNumber(),
                    request.getAnswers());
            response.setStatus(ConstantCode.SUBMIT_SUCCESS).setMsg("用户提交非编程问题成功");
        }catch (ElementDuplicateException | RecordNotExistException e) {
            response.setStatus(ConstantCode.SUBMIT_FAIL).setMsg(e.getMessage());
        }
        return response;
    }

}
