package com.kse.lpep.controller;

import com.kse.lpep.controller.vo.AbortProblemRequest;
import com.kse.lpep.controller.vo.BaseResponse;
import com.kse.lpep.controller.vo.ProgramSubmitRequest;
import com.kse.lpep.controller.vo.QuestionnaireSubmitRequest;
import com.kse.lpep.service.dto.JudgeResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 张舒韬
 * @since 2022/11/23
 */
@Controller
@RequestMapping("/submit")
public class SubmitController {

    /**
     * 对用户的程序提交请求的响应，运行并验证程序的正确性
     * @param request 用户请求
     * @return 程序提交和检测的结果
     * @see ProgramSubmitRequest
     * @see JudgeResult
     */
    public BaseResponse submitProgram(@RequestBody ProgramSubmitRequest request) {
        return new BaseResponse();
    }

    /**
     * 对用户放弃编程题请求的响应
     * @param request 用户请求
     * @return 请求执行结果
     */
    public BaseResponse abortProgram(@RequestBody AbortProblemRequest request) {
        return new BaseResponse();
    }

    /**
     * 对用户问卷阶段提交请求的响应
     * @param request 用户请求
     * @return 请求执行结果
     */
    public BaseResponse submitQuestionnaire(@RequestBody QuestionnaireSubmitRequest request) {
        return new BaseResponse();
    }
}
