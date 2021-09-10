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

    private String shopifyName;
    private String title;
    private String country;

    private Integer pageNum;
    private Integer pageSize;
}
