package com.kse.lpep.utils;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 张舒韬
 * @since 2022/11/27
 */
public class TimeMatcherTest {

    @Test
    public void testASPMatcher() {
        Pattern patternClingo = Pattern.compile("Time\\s*:\\s*([.0-9]*)s", Pattern.MULTILINE);
        Matcher matcher = patternClingo.matcher("clingo version 5.4.0\n" +
                "Reading from ...cal\\Temp\\aspTemp9879825421658199010.lp ...\n" +
                "Solving...\n" +
                "Answer: 1\n" +
                "penguin(tweety) bird(tweety) cat(tom) dog(spike)\n" +
                "SATISFIABLE\n" +
                "\n" +
                "Models       : 1\n" +
                "Calls        : 1\n" +
                "Time         : 0.005s (Solving: 0.00s 1st Model: 0.00s Unsat: 0.00s)\n" +
                "CPU Time     : 0.000s");

        System.out.println(matcher.find());
        System.out.println(Double.parseDouble(String.valueOf(matcher.group(1))));
    }
}
