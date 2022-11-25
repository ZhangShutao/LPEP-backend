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
public class AspResult {
    private boolean satisfiable;


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

    public static AspResult parseAspModel(String str) {
        // System.out.println("parsing");
        if (str.contains("UNSATISFIABLE")) {
            return new AspResult(false, str);
        } else if (str.contains("SATISFIABLE")) {
            return new AspResult(true, str);
        } else {
            throw new RuntimeException("解析ASP输出时发生语法错误");
        }
    }

    public AspResult(Boolean satisfiable, String str) {
        this.satisfiable = satisfiable;
        this.models = new HashSet<>();
        if (satisfiable)  {
            String[] lines = str.split(System.lineSeparator());
            for (int i = 0; i != lines.length; ++i) {
                if (lines[i].startsWith("Answer:")) {
                    AnswerSet answerSet = new AnswerSet(lines[i + 1]);
                    this.models.add(answerSet);
                }
            }
        }
    }

}
