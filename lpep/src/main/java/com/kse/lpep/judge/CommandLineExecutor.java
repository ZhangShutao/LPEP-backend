package com.kse.lpep.judge;

import com.kse.lpep.common.exception.UnsupportedOsTypeException;
import com.kse.lpep.judge.dto.CommandLineOutput;

import java.io.IOException;
import java.util.List;

/**
 * @author 张舒韬
 * @since 2022/11/23
 */
public interface CommandLineExecutor {
    /**
     * call a shell command
     * @param name name of command
     * @param params param list of command
     * @return execute result
     * @throws UnsupportedOsTypeException cannot tell the OS type
     * @throws IOException IO error happened
     */
    CommandLineOutput callShell(String name, List<String> params) throws UnsupportedOsTypeException, IOException;
}
