package com.macro.mall.common.api;

/**
 * 枚举了一些常用API操作码
 * Created by macro on 2019/4/19.
 */
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "Operation is successful"),
    FAILED(500, "The operation failure"),
    VALIDATE_FAILED(404, "Parameter verification failure"),
    UNAUTHORIZED(401, "No login is performed or the token has expired"),
    FORBIDDEN(403, "No relevant permissions");
    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
