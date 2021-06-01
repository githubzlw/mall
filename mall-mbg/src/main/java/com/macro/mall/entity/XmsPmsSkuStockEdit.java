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
 * 客户编辑商品保存sku的库存地方
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsPmsSkuStockEdit对象", description="客户编辑商品保存sku的库存地方")
public class XmsPmsSkuStockEdit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long productId;

    @ApiModelProperty(value = "sku编码")
    private String skuCode;

    private BigDecimal price;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "预警库存")
    private Integer lowStock;

    @ApiModelProperty(value = "展示图片")
    private String pic;

    @ApiModelProperty(value = "销量")
    private Integer sale;

    @ApiModelProperty(value = "单品促销价格")
    private BigDecimal promotionPrice;

    @ApiModelProperty(value = "锁定库存")
    private Integer lockStock;

    @ApiModelProperty(value = "商品销售属性，json格式")
    private String spData;

    @ApiModelProperty(value = "最小批量价格（新加字段）")
    private BigDecimal minPrice;

    @ApiModelProperty(value = "最大批量价格（新加字段）")
    private BigDecimal maxPrice;

    @ApiModelProperty(value = "原价格批量（新加字段）")
    private Integer moq;

    @ApiModelProperty(value = "最小价格批量（新加字段）")
    private Integer minMoq;

    @ApiModelProperty(value = "最大价格批量（新加字段）")
    private Integer maxMoq;

    @ApiModelProperty(value = "重量（新加字段）")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积长（新加字段）")
    private Integer volumeLenght;

    @ApiModelProperty(value = "体积宽（新加字段）")
    private Integer volumeWidth;

    @ApiModelProperty(value = "体积高（新加字段）")
    private Integer volumeHeight;

    @ApiModelProperty(value = "需求体积")
    private Double volume;

    @ApiModelProperty(value = "客户的ID")
    private Long memberId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private String plugStandard;

    private String shipsFrom;

    private BigDecimal cost;

    private String shipping;

    private BigDecimal comparedAtPrice;


}
