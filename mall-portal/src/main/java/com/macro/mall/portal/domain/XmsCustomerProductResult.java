package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-06-24
 */
@Data
@ApiModel("客户产品的查询结果")
public class XmsCustomerProductResult extends XmsCustomerProductQuery{



    private String skuData;
    private int pendingArrival;
    private int available;
    private int reserved;
    private int awaitingShipment;
    private int fulfilled;
    private Integer shippingFrom;


}
