package com.macro.mall.entity;

import java.math.BigDecimal;
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
 * 
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsShopifyOrderDetails对象", description="")
public class XmsShopifyOrderDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long orderNo;

    private Long variantId;

    private String title;

    private Integer quantity;

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

    private Integer productExists;

    private Long fulfillableQuantity;

    private Long grams;

    private BigDecimal price;

    private String totalDiscount;

    private String fulfillmentStatus;

    private String adminGraphqlApiId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "shopify详情的ID")
    @TableField("Line_item_id")
    private Long lineItemId;

    private String mainImg;


}
