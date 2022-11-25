package com.kse.lpep.judge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 张舒韬
 * @since 2022/11/23
 */
@Getter
@Setter
public class CommandLineOutput {
    private int exitValue;
    private String output;
    private String error;

    public CommandLineOutput(String output) {
        this.output = output;
    }

    public CommandLineOutput(String output, String error) {
        this.output = output;
        this.error = error;
    }

    public CommandLineOutput(int exitValue, String output, String error) {
        this.exitValue = exitValue;
        this.output = output;
        this.error = error;
    }

}
