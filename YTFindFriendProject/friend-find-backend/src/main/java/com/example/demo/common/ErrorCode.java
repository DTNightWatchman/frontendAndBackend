package com.example.demo.common;

/**
 * @Author: YT
 * @Description:
 * @DateTime: 2022/10/2$ - 23:25
 */
public enum  ErrorCode {

    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求为空", ""),
    NO_LOGIN(40100, "未登录", ""),
    NO_AUTH(40100, "无权限", ""),
    FORBIDDEN(40301, "禁止操作",""),
    SYSTEM_ERROR(50000, "系统内部异常", ""),
    ADD_ERROR(50001, "新增失败", ""),
    DELETE_ERROR(50002, "删除失败", ""),
    UPDATE_ERROR(50003, "更新失败", "");
    ;

    private final int code;

    private final String message;

    private final String description;


    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getMessage() {
        return message;
    }
}
