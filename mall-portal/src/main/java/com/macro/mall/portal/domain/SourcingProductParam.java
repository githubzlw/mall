package com.macro.mall.portal.domain;

import com.macro.mall.model.PmsProduct;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-05-27
 */
@Data
@ApiModel("Sourcing商品保存参数")
public class SourcingProductParam extends PmsProduct {
    @ApiModelProperty(value = "sourcing表的ID")
    private Long sourcingId;
    @ApiModelProperty(value = "product表的ID")
    private Long productId;

    @ApiModelProperty(value = "product的sku数据:list的字符串")
    private String skuList;

    @ApiModelProperty(value = "保存的字段")
    private String saveList;

    private String collectionId;
    private String productType;
    private String productTags;
}
