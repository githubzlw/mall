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
 * shopify客户的国家列表
 * </p>
 *
 * @author jack.luo
 * @since 2021-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsShopifyCountry对象", description="shopify客户的国家列表")
public class XmsShopifyCountry implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "店铺名称")
    private String shopifyName;

    @ApiModelProperty(value = "shopify的国家ID")
    private String countryId;

    @ApiModelProperty(value = "国家名称")
    private String name;

    @ApiModelProperty(value = "国家简码")
    private String code;

    private String tax;

    private String taxName;

    @ApiModelProperty(value = "省份")
    private String provinces;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
