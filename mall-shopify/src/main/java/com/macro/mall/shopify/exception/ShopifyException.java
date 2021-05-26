package com.macro.mall.shopify.exception;

import lombok.Data;

/**
 * @author jack.luo
 */
@Data
public class ShopifyException extends RuntimeException {

    private static final long serialVersionUID = -1864604160297181941L;

    private String code;
    private String detailedMessage;

    private ShopifyException() {
    }

    public ShopifyException(final String detailedMessage) {
        super(detailedMessage);
        this.detailedMessage = detailedMessage;
    }

    public ShopifyException(final String code, final String detailedMessage) {
        super(detailedMessage);
        this.code = code;
        this.detailedMessage = detailedMessage;
    }

    public ShopifyException(final Throwable t) {
        super(t);
    }


}
