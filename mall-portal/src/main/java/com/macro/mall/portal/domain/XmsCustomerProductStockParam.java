package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-06-24
 */
@Data
@ApiModel("客户产品的查询结果")
public class XmsCustomerProductStockParam {


    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    private static final long serialVersionUID = 665531L;

    private Integer pageNum;

    private Integer pageSize;

    private String title;

}
