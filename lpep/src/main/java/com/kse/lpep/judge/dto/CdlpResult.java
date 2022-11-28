package com.kse.lpep.judge.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 张舒韬
 * @since 2022/11/25
 */
@Getter
@Setter
@EqualsAndHashCode
public class CdlpResult {
    private Boolean satisfiable;

    private Set<CdlpModel> models;

    @Setter
    @Getter
    @EqualsAndHashCode
    public class CdlpModel {

        private Set<String> x;

        private Set<String> y;

        public CdlpModel(Set<String> x, Set<String> y) {
            this.x = x;
            this.y = y;
        }

        public CdlpModel(String str) {
            String[] parts = str.split("; ");
            String[] xPart = parts[0].replaceFirst("x:", "").split(", ");
            String[] yPart = parts[1].replaceFirst("y:", "").split(", ");

            this.x = new HashSet<>(Arrays.asList(xPart));
            this.y = new HashSet<>(Arrays.asList(yPart));
        }
    }

    public CdlpResult(Boolean satisfiable, String str) {
        this.satisfiable = satisfiable;
        this.models = new HashSet<>();
        if (satisfiable) {
            String[] lines = str.split(System.lineSeparator());
            for (int i = 0; i != lines.length; ++i) {
                if (lines[i].startsWith("default model")) {
                    this.models.add(new CdlpModel(lines[i + 1]));
                }
            }
        }
    }

    public static CdlpResult parseCdlpResult(String str) {
        if (str.contains("Satisfiable")) {
            return new CdlpResult(true, str);
        } else {
            return new CdlpResult(false, "");
        }
    }
}
