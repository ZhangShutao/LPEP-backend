package com.kse.lpep.service.impl;

import com.kse.lpep.config.WebConfig;
import com.kse.lpep.judge.SolverCallable;
import com.kse.lpep.service.IJudgeService;
import com.kse.lpep.service.dto.JudgeTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    /**
     * TODO 需要和标准输出目录下面的输出文件进行比较
     * @param task 待对比的任务
     * @return 测试任务中的输出与标准输出一致
     */
    private Boolean isJudgeResultCorrect(JudgeTask task) {
        if (task.getOutput() != null) {
            return !task.getOutput().contains("Unsatisfiable");
        }
        return false;
    }

    @Override
    public JudgeTask judge(JudgeTask task) {
        try {
            SolverCallable runnable = new SolverCallable(task);
            Future<JudgeTask> taskFuture = threadPoolExecutor.submit(runnable);
            task = taskFuture.get(5, TimeUnit.SECONDS);

            if (isJudgeResultCorrect(task)) {
                task.setState(JudgeTask.State.ACCEPTED);
            } else {
                task.setState(JudgeTask.State.WRONG);
            }

            return task;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            task.setState(JudgeTask.State.ABORTED);
            return task;
        }
    }
}
