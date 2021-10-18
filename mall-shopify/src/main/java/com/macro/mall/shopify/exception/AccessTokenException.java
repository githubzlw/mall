package com.macro.mall.shopify.exception;

import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.shopify.exception
 * @date:2021-10-18
 */
@Data
public class AccessTokenException extends RuntimeException {

    private static final long serialVersionUID = -491437852361941L;

    private String code;
    private String message;

    public AccessTokenException(final String message) {
        super(message);
        this.message = message;
    }

    public AccessTokenException(final String code, final String message) {
        super(message);
        this.code = code;
        this.message = message;
    }


    public AccessTokenException(final Throwable t) {
        super(t);
    }
}
