package com.macro.mall.portal.domain;

import com.macro.mall.entity.XmsTailFreight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.portal.domain
 * @date:2021-06-28
 */
@ApiModel(value="XmsTailFreight对象", description="尾程运费 的计算结果")
@Data
public class XmsTailFreightResult extends XmsTailFreight {

    @ApiModelProperty(value = "总价格")
    private double totalPrice;
}
