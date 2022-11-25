package com.kse.lpep.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author 张舒韬
 * @since 2022/11/22
 */
@Data
public class AbortProblemRequest {
    @NotBlank(message = "用户id不可为空")
    private String userId;

    @NotBlank(message = "问题id不可为空")
    private String problemId;
}
