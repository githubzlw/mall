package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class XmsCustomerProductParam implements Serializable {

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    private static final long serialVersionUID = 665531L;

    private Integer pageNum;

    private Integer pageSize;

    private String title;

    private List<String> shopifyPidList;

    @ApiModelProperty(value = "是否导入shopify标识")
    private Integer importFlag;
}