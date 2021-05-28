package com.macro.mall.common.exception;


/**
 * @author jack.luo
 * @date 2019/11/12
 */
public enum BizErrorCodeEnum implements ErrorCode {

    /**
     * 未指明的异常
     */
    UNSPECIFIED(500, "网络异常，请稍后再试"),
    NO_SERVICE(404, "网络异常, 服务器熔断"),

    // 通用异常
    REQUEST_ERROR(400, "入参异常,请检查入参后再次调用"),
    DESC_IS_NULL(4001, "DESC不能为空"),

    ITEM_IS_NULL(4002, "ITEM无数据"),
    BODY_IS_NULL(4003, "body is null"),

    FAIL(30000, "调用API返回错误"),
    EXPIRE_FAIL(30001, "你的授权已经过期"),
    LIMIT_EXCEED_FAIL(30002, "调用次数超过使用量");


    /**
     * 错误码
     */
    private final int code;

    /**
     * 描述
     */
    private final String description;

    /**
     * @param code 错误码
     * @param description 描述
     */
    private BizErrorCodeEnum(final int code, final String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据编码查询枚举。
     *
     * @param code 编码。
     * @return 枚举。
     */
    public static BizErrorCodeEnum getByCode(int code) {
        for (BizErrorCodeEnum value : BizErrorCodeEnum.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return UNSPECIFIED;
    }

    /**
     * 枚举是否包含此code
     * @param code 枚举code
     * @return 结果
     */
    public static Boolean contains(int code){
        for (BizErrorCodeEnum value : BizErrorCodeEnum.values()) {
            if (code == value.getCode()) {
                return true;
            }
        }
        return  false;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
