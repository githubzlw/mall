package com.macro.mall.dto;

import com.macro.mall.model.ProductSkuSaveEdit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 查询单个产品进行修改时返回的结果
 * Created by macro on 2018/4/26.
 */
@Data
public class PmsProductResult extends PmsProductParam {
    @ApiModelProperty("商品所选分类的父id")
    private Long cateParentId;


    @ApiModelProperty("已经铺货的shopifyID")
    private String shopifyPid;
    @ApiModelProperty("已经铺货的skuCode")
    private List<ProductSkuSaveEdit> skuList;

    private String collectionId;
    private String productType;
    private String productTags;



}
