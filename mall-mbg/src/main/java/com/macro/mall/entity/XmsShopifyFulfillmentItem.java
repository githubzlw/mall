package com.macro.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * shopify运单的item
 * </p>
 *
 * @author jack.luo
 * @since 2021-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsShopifyFulfillmentItem对象", description="shopify运单的item")
public class XmsShopifyFulfillmentItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String shopifyName;

    private Long fulfillmentId;

    private Long orderId;

    private Long itemId;

    private String variantId;

    private String title;

    private Integer quantity;

    private String price;

    private Integer grams;

    private String sku;

    private String variantTitle;

    private String vendor;

    private String fulfillmentService;

    private Long productId;

    private Integer requiresShipping;

    private Integer taxable;

    private Integer giftCard;

    private String name;

    private String variantInventoryManagement;

    private String properties;

    private Integer productExists;

    private Integer fulfillableQuantity;

    private String totalDiscount;

    private String fulfillmentStatus;

    private String taxLines;

    private String priceSet;

    private String duties;

    private String totalDiscountSet;

    private String discountAllocations;

    private String adminGraphqlApiId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
