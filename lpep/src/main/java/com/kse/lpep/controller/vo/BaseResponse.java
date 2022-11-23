package com.kse.lpep.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseResponse{
    private Integer status;
    private String msg;
    private Object data;
}
