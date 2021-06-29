package com.macro.mall.entity;

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
 * 尾程运费 的计算逻辑
 * </p>
 *
 * @author jack.luo
 * @since 2021-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsTailFreight对象", description="尾程运费 的计算逻辑")
public class XmsTailFreight implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "单位g 最小重量")
    private Double kgMin;

    @ApiModelProperty(value = "单位g 最大重量(包含)")
    private Double kgMax;

    @ApiModelProperty(value = "重量<=90720g直接取值，重量>90720取值：max(首重价+price * 重量,lowest_price)")
    private Double price;

    @ApiModelProperty(value = "最低价格")
    private Double lowestPrice;

    @ApiModelProperty(value = "首重价")
    private Double firstPrice;


}
