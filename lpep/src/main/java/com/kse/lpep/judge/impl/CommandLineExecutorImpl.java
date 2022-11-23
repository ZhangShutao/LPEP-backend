package com.kse.lpep.judge.impl;

import com.kse.lpep.common.exception.UnsupportedOsTypeException;
import com.kse.lpep.judge.CommandLineExecutor;
import com.kse.lpep.judge.dto.CommandLineOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author 张舒韬
 * @since 2022/11/23
 */
@Slf4j
public class CommandLineExecutorImpl implements CommandLineExecutor {
    public CommandLineExecutorImpl() {

    }

    /**
     * 获得当前操作系统调用命令的前缀
     * @return 添加在调用命令前的前缀
     * @throws UnsupportedOsTypeException 当前操作系统不是可支持的操作系统
     */
    private static List<String> getCommandLinePrefix() throws UnsupportedOsTypeException {
        List<String> commands = new ArrayList<>();
        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX) {
            commands.add("sh");
            commands.add("-c");

        } else if (SystemUtils.IS_OS_WINDOWS) {
            commands.add("cmd.exe");
            commands.add("/c");
        } else {
            throw new UnsupportedOsTypeException(System.getProperty("os.name"));
        }
        return commands;
    }

    /**
     * 获得输入流中的字符串
     * @param inputStream 输入流
     * @return 输入流中的字符串
     * @throws IOException 读取字符串时发生读写错误
     */
    private static String getTextFromInputStream(InputStream inputStream) throws IOException {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                joiner.add(line);
            }
        }

        return joiner.toString();
    }

    private static class ReadThread extends Thread {
        private InputStream inputStream;
        private String result;
        private IOException exception = null;

        ReadThread(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        String getResult() throws IOException {
            while (true) {
                if (!(isAlive())) break;
            }
            if (exception == null) {
                System.out.println(result);
                return result;
            } else {
                System.out.println(exception);
                throw exception;
            }
        }

        public void run () {
            try {
                result = getTextFromInputStream(inputStream);
            } catch (IOException e) {
                exception = e;
            }
        }
    }

    @Override
    public CommandLineOutput callShell(String name, List<String> params) throws UnsupportedOsTypeException, IOException {
        StringJoiner cmdJoiner = new StringJoiner(" ");
        cmdJoiner.add(name);
        params.forEach(cmdJoiner::add);
        log.info("call shell with the following command: {}", cmdJoiner);

        List<String> cmdArray = getCommandLinePrefix();
        cmdArray.add(name);
        cmdArray.addAll(params);

        Process process = Runtime.getRuntime().exec(cmdArray.toArray(new String[0]));
        ReadThread outputReader = new ReadThread(process.getInputStream());
        outputReader.start();
        ReadThread errorReader = new ReadThread(process.getErrorStream());
        errorReader.start();

        return new CommandLineOutput(outputReader.getResult(), errorReader.getResult());
    }
}
