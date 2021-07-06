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
public class XmsCustomerProductStock {


    private Integer productId;
    private String img;

    private Integer pendingArrival;
    private Integer available;
    private Integer reserved;
    private Integer awaitingShipment;
    private Integer fulfilled;

}
