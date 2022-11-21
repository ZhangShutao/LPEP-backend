package com.kse.lpep.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseResponse<T>{
    private Integer status;
    private String msg;
    private T data;
}
