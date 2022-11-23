package com.kse.lpep.controller.vo;

import lombok.Data;

/**
 * @author 张舒韬
 * @since 2022/11/22
 */
@Data
public class AbortProblemRequest {
    private String userId;

    private String problemId;
}
