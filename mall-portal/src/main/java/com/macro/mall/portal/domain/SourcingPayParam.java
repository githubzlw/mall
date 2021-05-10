package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-05-06
 */
@Data
@ApiModel("Sourcing支付订单的接受参数")
public class SourcingPayParam extends OrderPayParam{

    @ApiModelProperty("产品的ID")
    private Long productId;

    @ApiModelProperty("产品的sku编码:数量")
    private List<String> skuCodeAndNumList;

}
