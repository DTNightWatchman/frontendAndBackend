package com.example.demo.common;

/**
 * @Author: YT
 * @Description: 返回管理
 * @DateTime: 2022/10/2$ - 19:51
 */
public class ResultUtil {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(0, data, "ok");
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse(errorCode);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse(errorCode.getCode(), null, message, description);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode, String description) {
        return new BaseResponse(errorCode.getCode(),null, errorCode.getMessage(), description);
    }

    public static <T> BaseResponse<T> error(int code, String message, String description) {
        return new BaseResponse(code, null , message, description);
    }

}
