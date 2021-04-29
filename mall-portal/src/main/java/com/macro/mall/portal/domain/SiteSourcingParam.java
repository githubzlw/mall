package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importExpress.common.pojo
 * @date:2021-04-01
 */
@Data
@ApiModel(value = "网站buyForMe", description = "buyForMe的Bean")
public class SiteSourcingParam {
    @ApiModelProperty("BFM的商品URL")
    private String url;
    @ApiModelProperty("BFM的图片链接")
    private String img;

    @ApiModelProperty("BFM的商品名称")
    private String name;

    @ApiModelProperty("平均每日订单量")
    private Integer averageDailyOrder;
    @ApiModelProperty("仅一次订单量")
    private Integer oneTimeOrderOnly;
}
