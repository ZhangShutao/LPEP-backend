package com.kse.lpep.judge;

import com.kse.lpep.judge.impl.CommandLineExecutorImpl;
import com.kse.lpep.service.dto.JudgeTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 如果输出中有时间，则抽取并返回；如果没有，返回-1
     * @param string 待抽取的字符串
     * @return 时间
     */
    private double extractUsedTime(String string) {
        Pattern patternClingo = Pattern.compile("^Time\\S:\\s([.0-9]*)s");
        Pattern patternCdl = Pattern.compile("^Elapsed time: ([.0-9]*)s");
        Matcher matcherClingo = patternClingo.matcher(string);
        Matcher matcherCdl = patternCdl.matcher(string);

        if (matcherClingo.find()) {
            return Double.parseDouble(String.valueOf(matcherClingo.group()));
        } else if (matcherCdl.find()) {
            return Double.parseDouble(String.valueOf(matcherCdl.group()));
        } else {
            return -1;
        }
    }

    @Override
    public JudgeTask call() throws Exception {
        //log.info("task {} in solving", task.getProgSubmitId());
        TimedRunnable timedRunnable = new TimedRunnable(task);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        try {
            exec.submit(timedRunnable).get(task.getTimeLimit(), TimeUnit.SECONDS);

            if (task.getErrorMsg().contains("syntax error")) { // 错误信息中存在语法错误
                task.setStatus(JudgeTask.Status.SYNTAX_ERROR);

                log.error("第 {} 组数据运行时发生语法错误。", task.getCaseNumber());
            } else {
                double runnerSecond = extractUsedTime(task.getOutput());
                task.setRunnerTime(runnerSecond);
                task.setStatus(JudgeTask.Status.JUDGING);

                log.error("第 {} 组数据运行结束，待测试。", task.getCaseNumber());
            }

        } catch (InterruptedException | ExecutionException e) {
            task.setStatus(JudgeTask.Status.ABORTED);

            log.error("推理机任务 {} 被中断。", task.getCaseNumber());
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            task.setStatus(JudgeTask.Status.TIME_LIMIT_EXCEEDED);

            log.info("第 {} 组数据运行超时。", task.getCaseNumber());
        }
        return task;
    }
}
