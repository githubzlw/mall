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
@ApiModel(value="XmsTrafficFreightUnit对象", description="")
public class XmsTrafficFreightUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "运输方式")
    private String modeOfTransport;

    @ApiModelProperty(value = "交期时间")
    private String deliveryTime;

    @ApiModelProperty(value = "国家id")
    private Integer countryId;

    @ApiModelProperty(value = "默认重量g")
    private Double firstHeavy;

    @ApiModelProperty(value = "首重价")
    private BigDecimal firstHeavyPrice;

    @ApiModelProperty(value = "续重价格")
    private BigDecimal continuedHeavyPrice;

    @ApiModelProperty(value = "21kg以上/kg 运费")
    private BigDecimal bigHeavyPrice;

    @ApiModelProperty(value = "默认重量g")
    private Double defaultWeightOfSpecial;

    @ApiModelProperty(value = "特殊商品基础运费（带电，粉末）-首重")
    private BigDecimal firstHeavyPriceOfSpecial;

    @ApiModelProperty(value = "特殊商品基础运费（带电，粉末）-续重")
    private BigDecimal continuedHeavyPriceOfSpecial;

    @ApiModelProperty(value = "特殊商品运费（带电或者粉末）-大商品")
    private BigDecimal bigHeavyPriceOfSpecial;

    @ApiModelProperty(value = "0:正常状态，1：删除状态")
    private Integer del;

    @ApiModelProperty(value = "0:不需要拆包 1：需要拆包")
    private Integer split;

    @ApiModelProperty("总运费")
    private double totalFreight;

    @ApiModelProperty("我司成本运费")
    private double costAndFreightOfOurCompany;

    @ApiModelProperty("折扣价格")
    private double discountedTotalPrice;


    /**
     * 计算正常的总运费
     *
     * @param totalWeight
     * @return
     */
    public double calculateFreight(double totalWeight) {
        this.totalFreight = 0;
        if (totalWeight <= 0) {
            return this.totalFreight;
        }
        if (totalWeight <= 21) {
            this.totalFreight = calculateNormalFreight(totalWeight);
        } else {
            this.totalFreight = calculateBigFreight(totalWeight);
        }
        return this.totalFreight;
    }


    /**
     * 计算普通重量(<=21KG)
     *
     * @param totalWeight
     */
    public double calculateNormalFreight(double totalWeight) {
        BigDecimal tempTotalWeight = new BigDecimal(totalWeight);
        BigDecimal tempTotalFreight = BigDecimal.ZERO;

        double gradeWeight = 0.5;
        if (totalWeight > 0) {
            if (this.firstHeavy == gradeWeight) {
                tempTotalFreight = this.firstHeavyPrice.add(tempTotalWeight.divide(new BigDecimal(gradeWeight)).setScale(0, BigDecimal.ROUND_UP).subtract(new BigDecimal(1d)).multiply(this.continuedHeavyPrice));

                // tempTotalFreight = this.firstHeavyPrice.doubleValue() + Math.ceil(totalWeight / gradeWeight - 1) * this.continuedHeavyPrice.doubleValue();
            } else {
                tempTotalFreight = this.firstHeavyPrice.add(tempTotalWeight.subtract(new BigDecimal(this.firstHeavy)).divide(new BigDecimal(this.firstHeavy)).multiply(this.continuedHeavyPrice));

                //tempTotalFreight = this.firstHeavyPrice.doubleValue() + Math.ceil(totalWeight - this.firstHeavy) / this.firstHeavy * this.continuedHeavyPrice.doubleValue();
            }
        }
        if (null == tempTotalFreight || tempTotalFreight.doubleValue() <= 0) {
            tempTotalFreight = BigDecimal.ZERO;
        }
        return tempTotalFreight.doubleValue();
    }

    /**
     * 计算大重量(>21KG)
     *
     * @param totalWeight
     */
    public double calculateBigFreight(double totalWeight) {
        BigDecimal tempTotalWeight = new BigDecimal(totalWeight- this.firstHeavy / 1000);

        BigDecimal tempTotalFreight = this.firstHeavyPrice.add( tempTotalWeight.multiply(this.bigHeavyPrice) ).setScale(2, BigDecimal.ROUND_UP);

        if (null == tempTotalFreight || tempTotalFreight.doubleValue() <= 0) {
            tempTotalFreight = BigDecimal.ZERO;
        }
        // 计算成本
        this.costAndFreightOfOurCompany = calculateNormalFreight(totalWeight);
        return tempTotalFreight.doubleValue();
    }

}
