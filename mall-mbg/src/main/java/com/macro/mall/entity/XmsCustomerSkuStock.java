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
 * 客户的库存
 * </p>
 *
 * @author jack.luo
 * @since 2021-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsCustomerSkuStock对象", description="客户的库存,给客户下单保存的库存使用")
public class XmsCustomerSkuStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    @ApiModelProperty(value = "生成我司ID")
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

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "库存表ID")
    private Integer skuStockId;

    @ApiModelProperty(value = "0购买中 1已经购买 2 入库 3发货")
    private Integer status;

    @ApiModelProperty(value = "下单的订单号")
    private String orderNo;

    @ApiModelProperty(value = "下单的时候选择的国家 0china 1usa")
    private Integer shippingFrom;

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
    private BigDecimal volumeLenght;

    @ApiModelProperty(value = "体积宽（新加字段）")
    private BigDecimal volumeWidth;

    @ApiModelProperty(value = "体积高（新加字段）")
    private BigDecimal volumeHeight;

    @ApiModelProperty(value = "需求体积")
    private Double volume;


}
