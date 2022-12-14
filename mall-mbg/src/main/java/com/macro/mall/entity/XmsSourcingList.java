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
 * sourcing表
 * </p>
 *
 * @author jack.luo
 * @since 2021-06-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsSourcingList对象", description="sourcing表")
public class XmsSourcingList implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    @ApiModelProperty(value = "网址")
    private String url;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "价格")
    private String price;

    @ApiModelProperty(value = "运输方式")
    private String shipping;

    @ApiModelProperty(value = "橱窗图")
    private String images;

    @ApiModelProperty(value = "费用")
    private String cost;

    @ApiModelProperty(value = "状态：0->已接收；1->处理中；2->已处理 4->取消；5->无效数据； -1->删除；")
    private Integer status;

    @ApiModelProperty(value = "网站类型：1->ALIBABA;2->ALIEXPRESS;3->ESALIEXPRESS;4->AMAZON;5->WAYFAIR;6->EBAY;7->WALMART;8->ALI1688;9->IMG_ONLY;10->OTHER;11->SHOPIFY;")
    private Integer siteType;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "sku的json数据")
    private String skuJson;

    @ApiModelProperty(value = "生成我司ID")
    private Long productId;

    @ApiModelProperty(value = "货源链接")
    private String sourceLink;

    @ApiModelProperty(value = "运输方式  1进FBA, 2 进客户门点, 3 CIF")
    private Integer typeOfShipping;

    @ApiModelProperty(value = "选择类型 1:Drop Shipping  2:Wholesale and Bulk Shipping 3 Transportation Only:  4:Product Customization")
    private Integer chooseType;

    @ApiModelProperty(value = "目的国家")
    private String countryName;

    @ApiModelProperty(value = "目的州或者城市")
    private String stateName;

    @ApiModelProperty(value = "定制类型 1:changePackaging 2:changeColor,Material 3:Improve Quality 4:changeShape")
    private String customType;

    @ApiModelProperty(value = "国家id")
    private Integer countryId;

    @ApiModelProperty(value = "询问订单量")
    private Integer orderQuantity;

    @ApiModelProperty(value = "客户备注")
    private String remark;

    @ApiModelProperty(value = "1货源已处理")
    private Integer prcFlag;

    @ApiModelProperty(value = "aliexpress处理")
    private String pricePs;

    @ApiModelProperty(value = "cif的港口")
    private String cifPort;

    @ApiModelProperty(value = "amazon的fba地址")
    private String fbaWarehouse;

    @ApiModelProperty(value = "运费")
    private String shippingFee;

    @ApiModelProperty(value = "是否添加到私人商品表中 0未添加 1添加")
    private Integer addProductFlag;


}
