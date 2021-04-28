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
 * door to door的国家列表
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsListOfHomeDeliveryCountries对象", description="door to door的国家列表")
public class XmsListOfHomeDeliveryCountries implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer countryId;

    private String countryEn;

    @ApiModelProperty(value = "简码")
    private String shortCode;

    @ApiModelProperty(value = "州英文名称")
    private String stateEn;

    private String stateCn;

    private String zipCode;


}
