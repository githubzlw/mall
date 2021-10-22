package com.macro.mall.portal.domain;

import com.macro.mall.entity.XmsCustomerSkuStock;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.UmsMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-05-08
 */
@ApiModel("生成订单的参数")
@Data
@Builder
public class GenerateOrderParam {

    @ApiModelProperty("订单号")
    private String orderNo;
    @ApiModelProperty("总运费")
    private double totalFreight;
    @ApiModelProperty("客户信息")
    private UmsMember currentMember;
    @ApiModelProperty("下单库存数据")
    private List<PmsSkuStock> pmsSkuStockList;
    @ApiModelProperty("支付订单参数")
    private OrderPayParam orderPayParam;

    @ApiModelProperty("下单类型 0库存下单 1其他订单")
    private Integer type;

    @ApiModelProperty("已经购买的库存数据")
    private List<XmsCustomerSkuStock> customerSkuStockList;

    private Long shopifyOrderNo;

    @ApiModelProperty(value = "下单的时候选择的国家 0china 1usa")
    private Integer shippingFrom;

    @ApiModelProperty(value = "logo标识0或者null表示不贴1表示贴标识")
    private Integer logoFlag;

    private String logoUrl;
}
