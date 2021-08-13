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
 * shopify的商品主图信息
 * </p>
 *
 * @author jack.luo
 * @since 2021-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsShopifyPidImg对象", description="shopify的商品主图信息")
public class XmsShopifyPidImg implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "店铺名称")
    private String shopifyName;

    @ApiModelProperty(value = "商品的id")
    private String shopifyPid;

    @ApiModelProperty(value = "商品的主图")
    private String img;

    @ApiModelProperty(value = "商品的图片集合")
    private String imgInfo;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
