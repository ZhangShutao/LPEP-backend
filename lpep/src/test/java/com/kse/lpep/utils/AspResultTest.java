package com.kse.lpep.utils;

import com.kse.lpep.judge.dto.AspResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 张舒韬
 * @since 2022/11/25
 */
public class AspResultTest {

    @Test
    public void testParseSatisfiableAspResult() {
        try {
            String satisfiableOutput = LpepFileUtils.readFile("/lpep/test/satisfiable.out");
            System.out.println(satisfiableOutput);
            AspResult result = AspResult.parseAspModel(satisfiableOutput);
            assertTrue(result.isSatisfiable());
            assertEquals(4, result.getModels().size());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testParseUnsatifiableAspResult() {
        try {
            String unsatOutput = LpepFileUtils.readFile("/lpep/test/unsat.out");
            System.out.println(unsatOutput);
            AspResult result = AspResult.parseAspModel(unsatOutput);
            assertFalse(result.isSatisfiable());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void testCompareOfAspModels() {
        try {
            AspResult model1 = AspResult.parseAspModel(LpepFileUtils.readFile("/lpep/test/satisfiable.out"));
            AspResult model2 = AspResult.parseAspModel(LpepFileUtils.readFile("/lpep/test/satisfiable_2.out"));

            assertTrue(model1.equals(model2));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }
}
