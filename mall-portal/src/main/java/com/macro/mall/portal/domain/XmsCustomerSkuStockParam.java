package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class XmsCustomerSkuStockParam implements Serializable {

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    private static final long serialVersionUID = 665531L;

    private Integer pageNum;

    private Integer pageSize;


}