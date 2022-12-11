package com.kse.lpep.utils;

import com.kse.lpep.judge.dto.AspResult;
import com.kse.lpep.judge.dto.CdlpResult;
import com.kse.lpep.service.utils.LpepFileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author 张舒韬
 * @since 2022/12/11
 */
public class CdlpResultTest {

    @Test
    public void testCdlpResult() {
        try {
            CdlpResult model1 = CdlpResult.parseCdlpResult(LpepFileUtils.readFile("/lpep/test/satisfiable_3.out"));
            CdlpResult model2 = CdlpResult.parseCdlpResult(LpepFileUtils.readFile("/lpep/test/satisfiable_4.out"));

            assertTrue(model1.x_equals(model2));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
    }
}
