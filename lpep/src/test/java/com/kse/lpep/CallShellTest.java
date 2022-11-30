package com.kse.lpep;

import com.kse.lpep.common.exception.UnsupportedOsTypeException;
import com.kse.lpep.judge.CommandLineExecutor;
import com.kse.lpep.judge.dto.CommandLineOutput;
import com.kse.lpep.judge.impl.CommandLineExecutorImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class CallShellTest {

    private CommandLineExecutor commandLineExecutor = new CommandLineExecutorImpl();

    @Test
    void testCdl()  {
        try {
            CommandLineOutput output = commandLineExecutor.callShell("cdlsolver", new ArrayList<>());
            System.out.println("hello,cdlsolver");
            System.out.println(output.getError());
        } catch (UnsupportedOsTypeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private File saveProgramAsTempFile(String str) throws IOException {

        File programFile = File.createTempFile("aspTemp", ".lp");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(programFile.toPath())));
        writer.write(str);
        writer.flush();
        writer.close();
        return programFile;
    }

    @Test
    void testClingo()  {
        try {

            String program = "saeage";
            File file = saveProgramAsTempFile(program);

            List<String> myParam = new ArrayList<>();
            myParam.add(file.getAbsolutePath());
            CommandLineOutput output = commandLineExecutor.callShell("clingo 0 ", myParam);

            System.out.println(output.getExitValue());
            // System.out.println(output.getOutput());
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
