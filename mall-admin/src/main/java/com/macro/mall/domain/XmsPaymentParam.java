package com.macro.mall.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-05-10
 */
@Data
public class XmsPaymentParam {

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    @ApiModelProperty(value = "订单id")
    private String orderNo;

    @ApiModelProperty(value = "付款状态:0 失败(Failed) 1 成功(Success) 2进行中(Pending)")
    private Integer payStatus;

    @ApiModelProperty(value = "0是paypal支付，1 余额支付 ")
    private Integer payType;

    @ApiModelProperty(value = "支付来源 0未知 1采购库存 2sourcing下单 3充值")
    private Integer payFrom;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @ApiModelProperty(value = "分页行数")
    private Integer pageSize;

}
