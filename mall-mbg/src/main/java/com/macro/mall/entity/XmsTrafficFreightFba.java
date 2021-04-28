package com.macro.mall.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsTrafficFreightFba对象", description="")
public class XmsTrafficFreightFba implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "国家id")
    private Integer countryId;

    @ApiModelProperty(value = "运输方式")
    private String modeOfTransport;

    private String deliveryTime;

    @ApiModelProperty(value = "初始价，首重价")
    private BigDecimal firstHeavyPrice;

    @ApiModelProperty(value = "初始重量")
    private Double firstHeavy;

    private BigDecimal continuedHeavyPrice;

    @ApiModelProperty(value = "大于等于21kg运费")
    private BigDecimal freightOver21Price;

    private BigDecimal freightOver51Price;

    private BigDecimal freightOver101Price;

    private BigDecimal freightOver300Price;

    private BigDecimal freightOver501Price;

    private BigDecimal freightOver1001Price;

    @ApiModelProperty(value = "是否删除：1删除")
    private Integer del;


}
