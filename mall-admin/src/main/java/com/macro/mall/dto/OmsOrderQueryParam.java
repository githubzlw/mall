package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单查询参数
 * Created by macro on 2018/10/11.
 */
@Getter
@Setter
public class OmsOrderQueryParam {
    @ApiModelProperty(value = "订单编号")
    private String orderSn;
    @ApiModelProperty(value = "收货人姓名/号码")
    private String receiverKeyword;
    @ApiModelProperty(value = "订单状态：0->待付款；1->采购；2->入库；3->已发货；4->已完结；5->已经付款； -1/6->取消订单")
    private Integer status;
    @ApiModelProperty(value = "订单类型：0->正常订单；1->充值订单;2->发货订单")
    private Integer orderType;
    @ApiModelProperty(value = "订单来源：0->Sourcing购买库存订单(SC)；1->shopify发货订单(DG)")
    private Integer sourceType;
    @ApiModelProperty(value = "订单提交时间")
    private String createTime;
}
