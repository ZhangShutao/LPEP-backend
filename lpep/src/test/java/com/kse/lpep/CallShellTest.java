package com.kse.lpep;

import com.kse.lpep.common.exception.UnsupportedOsTypeException;
import com.kse.lpep.judge.CommandLineExecutor;
import com.kse.lpep.judge.dto.CommandLineOutput;
import com.kse.lpep.judge.impl.CommandLineExecutorImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;


public class CallShellTest {

    private CommandLineExecutor commandLineExecutor = new CommandLineExecutorImpl();

    @Test
    void testCall()  {
        try {
            CommandLineOutput output = commandLineExecutor.callShell("cdlsolver", new ArrayList<>());
            System.out.println("hello,world");
            System.out.println(output.getError());
        } catch (UnsupportedOsTypeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
