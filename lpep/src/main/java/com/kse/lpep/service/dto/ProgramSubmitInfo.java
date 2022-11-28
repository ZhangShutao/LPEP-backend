package com.kse.lpep.service.dto;

import com.kse.lpep.mapper.pojo.ProgSubmit;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 张舒韬
 * @since 2022/11/26
 */
@Data
public class ProgramSubmitInfo {

    private String id;

    private String status;

    private String submitTime;

    public static final Map<Integer, String> statusMap = new HashMap<Integer, String>(){{
        put(ProgSubmit.NOT_TESTED, "not tested");
        put(ProgSubmit.TESTING, "testing");
        put(ProgSubmit.ACCEPTED, "accepted");
        put(ProgSubmit.WRONG_ANSWER, "wrong answer");
        put(ProgSubmit.SYNTAX_ERROR, "syntax error");
        put(ProgSubmit.TIME_LIMIT_EXCEEDED, "time limit exceeded");
        put(ProgSubmit.ABORTED, "unknown error");
    }};

    public ProgramSubmitInfo(String id, String status, String submitTime) {
        this.id = id;
        this.status = status;
        this.submitTime = submitTime;
    }
}
