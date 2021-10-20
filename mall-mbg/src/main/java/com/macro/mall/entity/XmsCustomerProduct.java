package com.macro.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 客户的产品表
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsCustomerProduct对象", description="客户的产品表,给shopify使用")
public class XmsCustomerProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    @ApiModelProperty(value = "生成我司ID")
    private Long productId;

    @ApiModelProperty(value = "sourcing list表ID")
    private Long sourcingId;

    @ApiModelProperty(value = "货源链接")
    private String sourceLink;

    @ApiModelProperty(value = "状态：0-已确认；1-推送shopify；-1:无效数据 9 从shopfi同步")
    private Integer status;

    @ApiModelProperty(value = "sku的json数据")
    private String skuJson;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "从shopfi同步过来的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @ApiModelProperty(value = "我司提供给客户的价格(成本价格)")
    private String costPrice;

    @ApiModelProperty(value = "shopify的价格")
    private String shopifyPrice;

    @ApiModelProperty(value = "shopify的商品ID")
    private Long shopifyProductId;

    @ApiModelProperty(value = "shopify商品的完整数据")
    private String shopifyJson;

    @ApiModelProperty(value = "shopify的店铺名称")
    private String shopifyName;

    @ApiModelProperty(value = "shopify商品的标题")
    private String title;

    @ApiModelProperty(value = "shopify商品的图片链接")
    private String img;

    @ApiModelProperty(value = "购买库存数量")
    private Integer stockNum;

    @ApiModelProperty(value = "网站类型：1->ALIBABA;2->ALIEXPRESS;3->ESALIEXPRESS;4->AMAZON;5->WAYFAIR;6->EBAY;7->WALMART;8->ALI1688;9->IMG_ONLY;10->OTHER;11->SHOPIFY;")
    private Integer siteType;

    @ApiModelProperty(value = "地址信息")
    private String address;


    @ApiModelProperty(value = "shopify商品url")
    private String shopifyProductUrl;

    @ApiModelProperty(value = "sourcing导入标识 0未导入 1sourcing导入")
    private Integer importFlag;

}
