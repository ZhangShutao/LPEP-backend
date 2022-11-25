package com.kse.lpep.controller.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseResponse{
    private Integer status;
    private String msg;
    private Object data;

    public BaseResponse() {
    }

    public BaseResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public BaseResponse(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
}
