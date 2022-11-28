package com.kse.lpep.judge;

import com.kse.lpep.common.exception.UnsupportedOsTypeException;
import com.kse.lpep.judge.dto.CommandLineOutput;
import com.kse.lpep.judge.impl.CommandLineExecutorImpl;
import com.kse.lpep.service.dto.JudgeTask;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author 张舒韬
 * @since 2022/11/24
 */
@Slf4j
public class TimedRunnable implements Callable<JudgeTask> {

    private final CommandLineExecutor cmdExecutor = new CommandLineExecutorImpl();

    private final JudgeTask task;

    public TimedRunnable(JudgeTask task) {
        this.task = task;
    }

    public CommandLineOutput solveProgram(String solverName, List<String> args) {
        try {
            CommandLineOutput output = cmdExecutor.callShell(solverName, args);

            log.info("推理机输出为：{}", output.getOutput());
            log.info("推理机错误输出为：{}", output.getError());
            return output;
        } catch (IOException | UnsupportedOsTypeException e) {
            return new CommandLineOutput("", e.getMessage());
        }
    }
    @Override
    public JudgeTask call() throws Exception {

        if (task.getStatus() == JudgeTask.Status.RUNNING) {
            // log.info("task source file is {}", task.getSourcePath());
            if (task.getSourcePath() != null) {

                List<String> params = new ArrayList<>();
                params.add(task.getSourcePath());
                params.add(task.getInputPath());

                CommandLineOutput output = solveProgram(task.getCmd(), params);

                task.setOutput(output.getOutput());
                task.setErrorMsg(output.getError());
            }
        } else {
            log.error("测试任务 {} 状态错误", task.getCaseNumber());
            task.setStatus(JudgeTask.Status.ABORTED);
        }
        return task;
    }
}
