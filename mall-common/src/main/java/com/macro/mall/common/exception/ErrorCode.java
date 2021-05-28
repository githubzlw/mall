package com.macro.mall.common.exception;

/**
 * 错误码接口
 * @author jack.luo
 */
public interface ErrorCode {

    /**
     * 获取错误码
     *
     * @return
     */
    int getCode();

    /**
     * 获取错误信息
     *
     * @return
     */
    String getDescription();

}