package com.macro.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
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
 * 客户的产品表
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsCustomerProduct对象", description="客户的产品表")
public class XmsCustomerProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    @ApiModelProperty(value = "生成我司ID")
    private Long productId;

    @ApiModelProperty(value = "sourcing list表ID")
    private Integer sourcingId;

    @ApiModelProperty(value = "货源链接")
    private String sourceLink;

    @ApiModelProperty(value = "状态：0-已确认；1-推送shopify；-1:无效数据 9 从shopfi同步")
    private Integer status;

    @ApiModelProperty(value = "sku的json数据")
    private String skuJson;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "从shopfi同步过来的时间")
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


}
