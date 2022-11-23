package com.kse.lpep.judge;

import com.kse.lpep.common.exception.UnsupportedOsTypeException;
import com.kse.lpep.judge.dto.CommandLineOutput;
import com.kse.lpep.judge.impl.CommandLineExecutorImpl;
import com.kse.lpep.service.dto.JudgeTask;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.Callable;

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

    private File saveProgramAsTempFile(List<String> program) throws IOException {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (String s : program) {
            joiner.add(s);
        }

        File programFile = File.createTempFile("aspTemp", ".lp");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(programFile.toPath())));
        writer.write(joiner.toString());
        writer.flush();
        writer.close();
        return programFile;
    }

    public String solveProgram(String solverName, List<String> args, String program, String inputFilePath) {
        List<String> programs = new ArrayList<>();
        programs.add(program);
        try {
            File programFile = saveProgramAsTempFile(programs);
            List<String> params = new ArrayList<>(args);
            params.add(programFile.getAbsolutePath());
            params.add(inputFilePath);

            CommandLineOutput output = cmdExecutor.callShell(solverName, params);

            System.out.println(output.getOutput());
            return output.getOutput();
        } catch (IOException | UnsupportedOsTypeException e) {
            return e.toString();
        }
    }

    public String solveCDLProgram(String program, String inputFilePath) {
        return solveProgram("cdlsolver", new ArrayList<>(), program, inputFilePath);
    }

    public String solveASPProgram(String program, String inputFilePath) {
        List<String> args = new ArrayList<>();
        args.add("0");
        return solveProgram("clingo", args, program, inputFilePath);
    }

    @Override
    public JudgeTask call() throws Exception {
        log.info("task {} in solving", task.getProgSubmitId());

        if (task.getState() == JudgeTask.State.RUNNING) {

            log.info("task code is {}", task.getCode());
            if (task.getCode() != null) {

                for (String inputPath : task.getInputFilePaths()) {
                    String output;
                    if (task.getType() == JudgeTask.Type.ASP) {
                        output = solveASPProgram(task.getCode(), inputPath);
                    } else {
                        output = solveCDLProgram(task.getCode(), inputPath);
                    }
                    task.getOutput().add(output);

                    log.debug("The output of {} is {}", inputPath, output);
                }
                task.setState(JudgeTask.State.JUDGING);
                // TODO 数据库更新，修改提交状态
            }
        } else {
            System.out.println("task not runnable");
            task.setState(JudgeTask.State.ABORTED);
        }
        return task;
    }
}
