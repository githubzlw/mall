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
 * shopify的运单信息
 * </p>
 *
 * @author jack.luo
 * @since 2021-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsShopifyFulfillment对象", description="shopify的运单信息")
public class XmsShopifyFulfillment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String shopifyName;

    private Long fulfillmentId;

    private Long orderId;

    private String status;

    private String createdAt;

    private String service;

    private String updatedAt;

    private Date updateTm;

    private String trackingCompany;

    private String shipmentStatus;

    private String locationId;

    private String lineItems;

    private String trackingNumber;

    private String trackingNumbers;

    private String trackingUrl;

    private String trackingUrls;

    private String receipt;

    private String name;

    private String adminGraphqlApiId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
