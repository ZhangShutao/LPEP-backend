package com.kse.lpep.judge;

import com.kse.lpep.judge.impl.CommandLineExecutorImpl;
import com.kse.lpep.service.dto.JudgeTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author 张舒韬
 * @since 2022/11/23
 */
@Slf4j
public class SolverCallable implements Callable<JudgeTask> {
    private final CommandLineExecutor cmdExecutor = new CommandLineExecutorImpl();

    private final JudgeTask task;

    public SolverCallable(JudgeTask task) {
        this.task = task;
    }

    @Override
    public JudgeTask call() throws Exception {
        //log.info("task {} in solving", task.getProgSubmitId());
        TimedRunnable timedRunnable = new TimedRunnable(task);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        try {
            exec.submit(timedRunnable).get(task.getTimeLimit(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            task.setStatus(JudgeTask.Status.ABORTED);
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            task.setStatus(JudgeTask.Status.TIME_LIMIT_EXCEEDED);
        }
        return task;
    }
}
