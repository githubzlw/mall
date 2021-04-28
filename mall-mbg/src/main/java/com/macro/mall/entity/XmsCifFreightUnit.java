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
 * CIF的国家和运费
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsCifFreightUnit对象", description="CIF的国家和运费")
public class XmsCifFreightUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "国家ID")
    private Integer countryId;

    private String countryEn;

    private String countryCn;

    @ApiModelProperty(value = "港口名称")
    private String portName;

    @ApiModelProperty(value = "每体积运费")
    private Double freightPerVolume;

    @ApiModelProperty(value = "每公斤运费")
    private Double freightPerKilogram;

    @ApiModelProperty(value = "人名币汇率")
    private Double rmbRate;


}
