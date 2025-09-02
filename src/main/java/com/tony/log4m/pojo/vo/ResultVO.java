package com.tony.log4m.pojo.vo;

import lombok.Data;


@Data
public class ResultVO<T> {

    public static final int OK_CODE = 1;
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;

    public static final String OK_MSG = "操作成功";
    public static final String ERROR_MSG = "操作失败";

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

    /**
     * 成功响应（标准HTTP状态码）
     */
    public static <T> ResultVO<T> success() {
        return new ResultVO<>(SUCCESS_CODE, null, true, OK_MSG, null);
    }

    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(SUCCESS_CODE, null, true, OK_MSG, data);
    }

    public static <T> ResultVO<T> success(T data, String msg) {
        return new ResultVO<>(SUCCESS_CODE, null, true, msg, data);
    }

    /**
     * 错误响应
     */
    public static <T> ResultVO<T> error() {
        return new ResultVO<>(ERROR_CODE, null, false, ERROR_MSG, null);
    }

    public static <T> ResultVO<T> error(String msg) {
        return new ResultVO<>(ERROR_CODE, null, false, msg, null);
    }

    public static <T> ResultVO<T> error(Integer code, String msg) {
        return new ResultVO<>(code, null, false, msg, null);
    }

    // -------------------------------------------- 错误码 --------------------------------------------

    public static <T> ResultVO<T> error(ResultVO<?> resultVO) {
        return new ResultVO<>(resultVO.getCode(), resultVO.getLevel(), resultVO.getOk(), resultVO.getMsg(), null);
    }

}