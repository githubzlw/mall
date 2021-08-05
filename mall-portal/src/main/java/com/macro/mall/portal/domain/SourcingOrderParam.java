package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 生成订单时传入的参数
 * Created by macro on 2018/8/30.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SourcingOrderParam extends OrderPayParam {

    @ApiModelProperty("被选中的购物车商品ID")
    private List<Long> cartIds;

    private Integer numTotal;

    private Double weightTotal;
    private Double volumeTotal;
    private Double productPriceTotal;
    private Double discountPrice;
    private Double priceTotal;

    private String modeOfTransport;
    private String locationType;

    @ApiModelProperty("购物车Json信息")
    private String carInfo;

    private String invCountry;


    private String shippingMethodRadioIndex;
    private String fbaSelsect;
    private String isLabelingFree;
    private String shippingMethodName;
    private String shipTo;
    private String shippingRadio;
    private String allCountryOptions;

}
