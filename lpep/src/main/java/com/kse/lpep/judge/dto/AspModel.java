package com.kse.lpep.judge.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author 张舒韬
 * @since 2022/11/25
 */
@Getter
@Setter
@EqualsAndHashCode
public class AspModel {
    private boolean satisfiable;

    private boolean syntaxError;

    private List<String> syntaxMsg;

    private Set<AnswerSet> models;

    @Data
    public class AnswerSet {
        public Set<String> literals;

        public AnswerSet(Set<String> literals) {
            this.literals = literals;
        }

        public AnswerSet(String str) {
            this.literals = new HashSet<>(Arrays.asList(str.split(" ")));
        }
    }

    public static AspModel parseAspModel(String str) {
        // System.out.println("parsing");
        if (str.contains("UNSATISFIABLE")) {
            return new AspModel(false, str);
        } else if (str.contains("SATISFIABLE")) {
            return new AspModel(true, str);
        } else {
            throw new RuntimeException("解析ASP输出时发生语法错误");
        }
    }

    public static List<String> parseSyntaxError(String str) {
        String[] lines = str.split(System.lineSeparator());
        List<String> errors = new ArrayList<>();
        for (String line : lines) {
            if (line.contains("syntax error")) {
                errors.add(line);
            }
        }
        return errors;
    }

    public AspModel(Boolean satisfiable, String str) {
        if (!satisfiable) {
            this.satisfiable = false;
            this.models = new HashSet<>();
        } else {
            this.satisfiable = true;
            String[] lines = str.split(System.lineSeparator());
            this.models = new HashSet<>();
            for (int i = 0; i != lines.length; ++i) {
                if (lines[i].startsWith("Answer:")) {
                    AnswerSet answerSet = new AnswerSet(lines[i + 1]);
                    this.models.add(answerSet);
                }
            }
        }
    }

}
