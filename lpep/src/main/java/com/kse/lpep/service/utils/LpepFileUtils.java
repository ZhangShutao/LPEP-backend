package com.kse.lpep.service.utils;

import org.apache.catalina.util.URLEncoder;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
        // 将读入的字符串转变为utf-8编码
        String fileNameUTF = new String(fileName.getBytes("utf-8"), "utf-8");
        InputStream inputStream = Files.newInputStream(new File(fileNameUTF).toPath());

//        InputStream inputStream = Files.newInputStream(new File(fileName).toPath());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        String line;
        while ((line = reader.readLine()) != null) {
            joiner.add(new String(line.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
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
