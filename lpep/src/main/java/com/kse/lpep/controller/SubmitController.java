package com.kse.lpep.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.kse.lpep.common.ConstantCode;
import com.kse.lpep.common.exception.NoSuchRecordException;
import com.kse.lpep.common.exception.NotAuthorizedException;
import com.kse.lpep.common.utility.ValidUtil;
import com.kse.lpep.controller.vo.AbortProblemRequest;
import com.kse.lpep.controller.vo.BaseResponse;
import com.kse.lpep.controller.vo.ProgramSubmitRequest;
import com.kse.lpep.controller.vo.QuestionnaireSubmitRequest;
import com.kse.lpep.service.IExperService;
import com.kse.lpep.service.IJudgeService;
import com.kse.lpep.service.ISubmitService;
import com.kse.lpep.service.dto.JudgeResult;
import com.kse.lpep.service.dto.JudgeTask;
import com.kse.lpep.service.dto.ProgramSubmitInfo;
import com.kse.lpep.service.dto.ProgramSubmitInfoPage;
import com.kse.lpep.utils.LpepFileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;
import java.util.List;

/**
 * @author 张舒韬
 * @since 2022/11/23
 */
@RestController
@RequestMapping("/submit")
public class SubmitController {

    @Autowired
    private ISubmitService submitService;

    @Autowired
    private IJudgeService judgeService;



    /**
     * 对用户的程序提交请求的响应，运行并验证程序的正确性
     * todo user_time需要和足迹中的时间做运算
     * @param request 用户请求
     * @return 程序提交和检测的结果
     * @see ProgramSubmitRequest
     * @see JudgeResult
     */
    @Transactional
    @PostMapping("/prog_submit")
    public BaseResponse submitProgram(@RequestBody @Valid ProgramSubmitRequest request,
                                      BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            String errorMessage = ValidUtil.getValidErrorMessage(bindingResult);
            return new BaseResponse(ConstantCode.VALID_FAIL, errorMessage);
        }
        try {
            List<JudgeTask> taskList = submitService.submitProgram(request.getUserId(),
                    request.getQuestionId(),
                    request.getSource());

            for (JudgeTask task : taskList) {
                task.setStatus(JudgeTask.Status.RUNNING);
                judgeService.judge(task);

                if (task.getStatus() == JudgeTask.Status.ACCEPTED) {
                    JudgeResult result = new JudgeResult();
                    result.setStatus(JudgeResult.Status.ACCEPTED);
                    submitService.modifyProgUserFootprint(request.getUserId(), request.getQuestionId());
                    return new BaseResponse(ConstantCode.SUBMIT_SUCCESS, "测试通过。", result);
                } else {
                    JudgeResult result = writeErrorToResult(task);
                    String message;
                    if (task.getStatus() == JudgeTask.Status.TIME_LIMIT_EXCEEDED) {
                        message = String.format("第 %d 组数据运行超时。", task.getCaseNumber());
                        result.setStatus(JudgeResult.Status.TIME_LIMIT_EXCEEDED);
                    } else if (task.getStatus() == JudgeTask.Status.WRONG) {
                        message = String.format("第 %d 组数据运行错误。", task.getCaseNumber());
                        result.setStatus(JudgeResult.Status.WRONG_ANSWER);
                    } else if (task.getStatus() == JudgeTask.Status.SYNTAX_ERROR) {
                        message = "输出程序存在语法错误。";
                        result.setStatus(JudgeResult.Status.SYNTAX_ERROR);
                    } else {
                        message = "错误类型未知。";
                        result.setStatus(JudgeResult.Status.UNKNOWN_ERROR);
                    }

                    return new BaseResponse(ConstantCode.SUBMIT_FAIL, message, result);
                }
            }
        } catch (NoSuchRecordException | NotAuthorizedException | IOException e) {
            JudgeResult result = new JudgeResult();
            result.setStatus(JudgeResult.Status.UNKNOWN_ERROR);
            return new BaseResponse(ConstantCode.SUBMIT_FAIL, e.getMessage(), result);
        }
        return new BaseResponse();
    }

    /**
     * 对用户放弃编程题请求的响应
     * @param request 用户请求
     * @return 请求执行结果
     * @see AbortProblemRequest
     */
    @Transactional
    @PostMapping("submit_abort")
    public BaseResponse abortProgram(@RequestBody @Valid AbortProblemRequest request, BindingResult bindingResult) {
        BaseResponse response = new BaseResponse();
        if (bindingResult.hasErrors()) {
            String message = bindingResult.toString();
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(message);
        } else {
            try {
                if (submitService.abortProgram(request.getUserId(), request.getProblemId())) {
                    submitService.modifyProgUserFootprint(request.getUserId(), request.getProblemId());
                    response.setStatus(ConstantCode.SUBMIT_SUCCESS);
                } else {
                    response.setStatus(ConstantCode.SUBMIT_FAIL);
                }
            } catch (NotAuthorizedException | NoSuchRecordException e) {
                response.setStatus(ConstantCode.VALID_FAIL).setMsg(e.getMessage());
            }
        }
        return response;
    }

    /**
     * 对用户问卷阶段提交请求的响应
     * @param request 用户请求
     * @return 请求执行结果
     */
    @PostMapping("submit_questionnaire")
    public BaseResponse submitQuestionnaire(@RequestBody @Valid QuestionnaireSubmitRequest request,
                                            BindingResult bindingResult) {
        BaseResponse response = new BaseResponse();
        if (bindingResult.hasErrors()) {
            String message = bindingResult.toString();
            response.setStatus(ConstantCode.VALID_FAIL).setMsg(message);
        } else {
            try {
                if (submitService.submitQuestionnaire(request.getUserId(), request.getPhaseId(), request.getAnswer())) {
                    response.setStatus(ConstantCode.SUBMIT_SUCCESS);
                } else {
                    response.setStatus(ConstantCode.SUBMIT_FAIL).setMsg("未知错误");
                }
            } catch (NoSuchRecordException | NotAuthorizedException e) {
                response.setStatus(ConstantCode.VALID_FAIL).setMsg(e.getMessage());
            }
        }
        return response;
    }

    /**
     * 分页查询一个用户对指定问题的所有提交记录
     * @param userId 用户id
     * @param questionId 问题id
     * @param pageIndex 页码
     * @param pageSize 页面大小
     * @return 包含ProgramSubmitInfoPage的响应
     * @see com.kse.lpep.service.dto.ProgramSubmitInfo
     * @see com.kse.lpep.service.dto.ProgramSubmitInfoPage
     */
    @GetMapping("/list_prog_submit")
    public BaseResponse listProgSubmit(@RequestParam(value = "userId") String userId,
                                       @RequestParam(value = "questionId") String questionId,
                                       @RequestParam(value = "pageIndex") int pageIndex,
                                       @RequestParam(value = "pageSize") int pageSize) {
        BaseResponse response = new BaseResponse();
        try {
            List<ProgramSubmitInfo> infoList = submitService.listProgramSubmitInfo(userId, questionId, pageIndex, pageSize);
            response.setStatus(ConstantCode.QUERY_SUCCESS);
            response.setData(new ProgramSubmitInfoPage(infoList));
        } catch (NotAuthorizedException e) {
            response.setMsg(e.getMessage());
            response.setStatus(ConstantCode.VALID_FAIL);
        } catch (NoSuchRecordException e) {
            response.setMsg(e.getMessage());
            response.setStatus(ConstantCode.QUERY_FAIL);
        }
        return response;
    }

    private JudgeResult writeErrorToResult(JudgeTask task) {
        JudgeResult result = new JudgeResult();

        try {
            if (task.getStatus() == JudgeTask.Status.SYNTAX_ERROR) {
                result.setErrorMsg(task.getOutput());
                result.setStatus(JudgeResult.Status.SYNTAX_ERROR);
            } else if (task.getStatus() == JudgeTask.Status.ABORTED) {
                result.setErrorMsg(task.getErrorMsg());
                result.setStatus(JudgeResult.Status.UNKNOWN_ERROR);
            } else if (task.getStatus() == JudgeTask.Status.WRONG) {
                result.setNumberOfWrong(task.getCaseNumber());
                result.setWrongCaseInput(LpepFileUtils.readFile(task.getInputPath()));
                result.setStandardOutput(LpepFileUtils.readFile(task.getStandardOutputPath()));
                result.setUserOutput(task.getOutput());
                result.setStatus(JudgeResult.Status.WRONG_ANSWER);
            } else if (task.getStatus() == JudgeTask.Status.TIME_LIMIT_EXCEEDED) {
                result.setNumberOfWrong(task.getCaseNumber());
                result.setWrongCaseInput(LpepFileUtils.readFile(task.getInputPath()));
                result.setStatus(JudgeResult.Status.TIME_LIMIT_EXCEEDED);
            }
        } catch (IOException e) {
            result.setStatus(JudgeResult.Status.UNKNOWN_ERROR);
        }

        return result;
    }

}
