package com.kse.lpep.utils;

import com.kse.lpep.common.exception.UnsupportedOsTypeException;
import com.kse.lpep.judge.CommandLineExecutor;
import com.kse.lpep.judge.dto.CommandLineOutput;
import com.kse.lpep.judge.impl.CommandLineExecutorImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author 张舒韬
 * @since 2022/11/25
 */
public class CommandLineTest {
    private final CommandLineExecutor commandLineExecutor = new CommandLineExecutorImpl();

    @Test
    public void testSyntaxAspWithClingo() {
        List<String> params = new ArrayList<>();
        params.add("0");
        params.add("/lpep/test/syntax_error.lp");
        try {
            CommandLineOutput output = commandLineExecutor.callShell("clingo", params);
            System.out.println(output.getError());
        } catch (UnsupportedOsTypeException | IOException e) {
            fail();
        }
    }
}
