package com.example.api;

import lombok.Data;

@Data
public class BaseResponse<T> {

    private Integer code;
    private String msg;
    private T date;

    public BaseResponse(StatusCode code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }
    public BaseResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public BaseResponse(Integer code, String msg, T date) {
        this.code = code;
        this.msg = msg;
        this.date = date;
    }
}
