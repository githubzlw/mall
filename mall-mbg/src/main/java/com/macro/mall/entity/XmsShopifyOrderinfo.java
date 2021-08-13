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
 * 
 * </p>
 *
 * @author jack.luo
 * @since 2021-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsShopifyOrderinfo对象", description="")
public class XmsShopifyOrderinfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long orderNo;

    private String shopifyName;

    private String email;

    private String closedAt;

    private String createdAt;

    private String updatedAt;

    private Integer number;

    private String note;

    private String token;

    private String gateway;

    private Integer test;

    private String totalPrice;

    private String subtotalPrice;

    private Integer totalWeight;

    private String totalTax;

    private Integer taxesIncluded;

    private String currency;

    private String financialStatus;

    private Integer confirmed;

    private String totalDiscounts;

    private String totalLineItemsPrice;

    private String cartToken;

    private Integer buyerAcceptsMarketing;

    private String name;

    private String referringSite;

    private String landingSite;

    private String cancelledAt;

    private String cancelReason;

    private String totalPriceUsd;

    private String checkoutToken;

    private String reference;

    private String userId;

    private String locationId;

    private String sourceIdentifier;

    private String sourceUrl;

    private String processedAt;

    private String deviceId;

    private String phone;

    private String customerLocale;

    private String appId;

    private String browserIp;

    private String landingSiteRef;

    private Integer orderNumber;

    private String processingMethod;

    private Long checkoutId;

    private String sourceName;

    private String fulfillmentStatus;

    private String tags;

    private String contactEmail;

    private String orderStatusUrl;

    private String presentmentCurrency;

    private String adminGraphqlApiId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "运单服务的ID")
    private Long fulfillmentServiceId;

    @ApiModelProperty(value = "运费")
    private String totalShippingPrice;


}
