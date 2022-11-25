package com.kse.lpep.utils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author 张舒韬
 * @since 2022/11/24
 */
public class LpepFileUtils {
    public static String readFile(String fileName) throws IOException {
        InputStream inputStream = Files.newInputStream(new File(fileName).toPath());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        String line;
        while ((line = reader.readLine()) != null) {
            joiner.add(line);
        }
        return joiner.toString();
    }

    public static List<String> readFileByLines(String fileName) throws IOException {
        InputStream inputStream = Files.newInputStream(new File(fileName).toPath());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> input = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            input.add(line);
        }
        return input;
    }
}
