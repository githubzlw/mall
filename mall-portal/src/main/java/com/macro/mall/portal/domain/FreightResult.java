package com.macro.mall.portal.domain;

import com.macro.mall.entity.XmsTrafficFreightUnit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: 运费计算bean
 * @date:2021-04-14
 */
@Data
@ApiModel(value = "计算运费的参数Bean")
public class FreightResult {

    @ApiModelProperty("国家ID")
    private int countryId;

    @ApiModelProperty("商品总价格")
    private double productCost;

    @ApiModelProperty("总重量")
    private double totalWeight;

    @ApiModelProperty("普通运费结果集合")
    private List<XmsTrafficFreightUnit> unitList;

    @ApiModelProperty("免邮标识 1免邮 0非免邮")
    private int b2cFlag;

    @ApiModelProperty("商品总数量")
    private int totalNum;

}


