package com.example.demo.exception;

import cn.hutool.core.text.escape.XmlEscape;
import com.example.demo.common.ErrorCode;

/**
 * @Author: YT
 * @Description: 异常处理
 * @DateTime: 2022/10/3$ - 11:29
 */
public class BusinessException extends RuntimeException {

    private final int code;

    private final String description;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;

    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
