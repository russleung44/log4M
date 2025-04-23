package com.tony.log4m.pojo.vo;

import lombok.Data;


@Data
public class ResultVO<T> {

    public static final int OK_CODE = 1;

    public static final String OK_MSG = "操作成功";

    private Integer code;

    private String level;

    private String msg;

    private Boolean ok;

    private T data;


    public ResultVO(Integer code, String level, boolean ok, String msg, T data) {
        this.code = code;
        this.level = level;
        this.ok = ok;
        this.msg = msg;
        this.data = data;
    }

    public ResultVO(Integer code, String level, boolean ok, String msg) {
        this.code = code;
        this.level = level;
        this.ok = ok;
        this.msg = msg;
    }

    public static <T> ResultVO<T> ok() {
        return new ResultVO<>(OK_CODE, null, true, OK_MSG, null);
    }

    public static <T> ResultVO<T> ok(T data) {
        return new ResultVO<>(OK_CODE, null, true, OK_MSG, data);
    }

    public static <T> ResultVO<T> okMsg(String msg) {
        return new ResultVO<>(OK_CODE, null, true, msg, null);
    }


    // -------------------------------------------- 错误码 --------------------------------------------


    public static <T> ResultVO<T> error(ResultVO<?> resultVO) {
        return new ResultVO<>(resultVO.getCode(), resultVO.getLevel(), resultVO.getOk(), resultVO.getMsg(), null);
    }


}