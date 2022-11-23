package com.kse.lpep.service;

import com.kse.lpep.service.dto.JudgeTask;

/**
 * @author 张舒韬
 * @since 2022/11/23
 */
public interface IJudgeService {
    /**
     * 运行并测试用户提交的程序，并将测试结果写入数据库
     * @param task 根据用户提交生成的测试任务
     * @return 修改了状态、输出、错误信息的测试任务对象
     */
    JudgeTask judge(JudgeTask task);
}
