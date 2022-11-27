package com.kse.lpep.service.impl;

import com.kse.lpep.config.WebConfig;
import com.kse.lpep.judge.SolverCallable;
import com.kse.lpep.judge.dto.AspResult;
import com.kse.lpep.judge.dto.CdlpResult;
import com.kse.lpep.mapper.IProgSubmitMapper;
import com.kse.lpep.mapper.pojo.ProgSubmit;
import com.kse.lpep.service.IJudgeService;
import com.kse.lpep.service.dto.JudgeTask;
import com.kse.lpep.utils.LpepFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author 张舒韬
 * @since 2022/11/23
 */
@Service
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JudgeServiceImpl implements IJudgeService {

    @Resource(name = WebConfig.THREAD_POOL)
    private ThreadPoolTaskExecutor threadPoolExecutor;

    @Autowired
    private IProgSubmitMapper progSubmitMapper;

    /**
     * 将用户的输出和标准输出目录下面的输出文件进行比较，根据标准输出文件的首行注释
     * @param task 待对比的任务
     * @return 测试任务中的输出与标准输出一致
     */
    private Boolean isJudgeResultCorrect(JudgeTask task) throws IOException {
        if (task.getOutput() != null) {
            String standardOutput = LpepFileUtils.readFile(task.getStandardOutputPath());
            if (standardOutput.startsWith("clingo")) {
                return isAspResultCorrect(standardOutput, task.getOutput());
            } else if (standardOutput.startsWith("CDLSolver")) {
                return isCdlpResultCorrect(standardOutput, task.getOutput());
            }
//            return !task.getOutput().contains("Unsatisfiable");
        }
        return false;
    }

    private Boolean isAspResultCorrect(String stdOutput, String userOutput) {
        AspResult stdResult = AspResult.parseAspModel(stdOutput);
        AspResult userResult = AspResult.parseAspModel(userOutput);
        return stdResult.equals(userResult);
    }

    private Boolean isCdlpResultCorrect(String stdOutput, String userOutput) {
        CdlpResult stdResult = CdlpResult.parseCdlpResult(stdOutput);
        CdlpResult userResult = CdlpResult.parseCdlpResult(userOutput);
        return stdResult.equals(userResult);
    }

    @Override
    public JudgeTask judge(JudgeTask task) {
        try {
            SolverCallable runnable = new SolverCallable(task);
            Future<JudgeTask> taskFuture = threadPoolExecutor.submit(runnable);
            task = taskFuture.get(30, TimeUnit.SECONDS);

            if (task.getStatus() == JudgeTask.Status.JUDGING) {
                if (isJudgeResultCorrect(task)) {
                    task.setStatus(JudgeTask.Status.ACCEPTED);
                    progSubmitMapper.updateStatusById(task.getProgSubmitId(), ProgSubmit.ACCEPTED);
                } else {
                    task.setStatus(JudgeTask.Status.WRONG);
                    progSubmitMapper.updateStatusById(task.getProgSubmitId(), ProgSubmit.WRONG_ANSWER);
                }
            }

            return task;

        } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
            task.setStatus(JudgeTask.Status.ABORTED);
            progSubmitMapper.updateStatusById(task.getProgSubmitId(), ProgSubmit.UNKNOWN_ERROR);
            return task;
        }
    }
}
