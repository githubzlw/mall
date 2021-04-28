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
 * FBA的国家
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsListOfFbaCountries对象", description="FBA的国家")
public class XmsListOfFbaCountries implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer countryId;

    private String countryEn;

    private String fbaCode;

    private String address;

    private String city;

    private String state;

    private String zip;


}
