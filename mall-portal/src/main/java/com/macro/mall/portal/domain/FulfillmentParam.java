package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-09-09
 */
@Data
@ApiModel("运单参数")
public class FulfillmentParam {
    @ApiModelProperty("开始时间")
    private String beginTime;
    @ApiModelProperty("结束时间")
    private String endTime;
    @ApiModelProperty("运单号")
    private String trackingNumber;

    @ApiModelProperty("必填-shopify店铺名称")
    private String shopifyName;
    private String title;
    private String country;
    private String status;

    @ApiModelProperty("状态:in transit出运中 pickup 待收货 delivered已签收 expired超期 undelivered未收到 other其他")
    private String shipmentStatus;

    private Integer pageNum;
    private Integer pageSize;


    @ApiModelProperty("必填-shopify订单号")
    private Long orderNo;

    @ApiModelProperty("必填-运输公司")
    private String trackingCompany;

    private Long memberId;

    @ApiModelProperty("是否通知客户")
    private boolean notifyCustomer;

    @ApiModelProperty("运单消息")
    private String message;

    @ApiModelProperty("位置Id")
    private String locationId;
}
