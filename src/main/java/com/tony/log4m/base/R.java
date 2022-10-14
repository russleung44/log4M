package com.tony.log4m.base;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("通用返回类")
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("状态码 1:成功 -1:失败")
    private int code = 1;
    @ApiModelProperty("返回消息")
    private String msg = "成功";
    @ApiModelProperty("承载数据")
    private T data;
    @ApiModelProperty("是否展示信息")
    private Boolean show = false;

    public R() {
        super();
    }

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.show = true;
    }

    public R(int code, String msg, boolean show) {
        this.code = code;
        this.msg = msg;
        this.show = show;
    }

    public R(T data, int code, String msg, boolean show) {
        this.data = data;
        this.code = code;
        this.msg = msg;
        this.show = show;
    }

    public R(T data, String msg) {
        this.data = data;
        this.msg = msg;
    }


    protected R(T data) {
        this.data = data;
    }

    public R(String msg, boolean show) {
        this.msg = msg;
        this.show = show;
    }

    public static <T> R<T> ok(T data) {
        return new R(data);
    }

    public static <T> R<T> ok(T data, String msg) {
        return new R(data, msg);
    }

    public static <T> R<T> ok() {
        return new R<>();
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg);
    }

    public static R fail(int code, String message, boolean show) {
        return new R(code, message, show);
    }

    public static <T> R<T> fail(T data, int code, String message, boolean show) {
        return new R(data, code, message, show);
    }

    public static R fail(String message) {
        return new R(-1, message);
    }

    public static R fail(String msg, boolean show) {
        return new R(msg, show);
    }

    public R setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
