package com.macro.mall.entity;

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
 * shopify订单地址
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsShopifyOrderAddress对象", description="shopify订单地址")
public class XmsShopifyOrderAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long orderNo;

    private String firstName;

    private String address1;

    private String phone;

    private String city;

    private String zip;

    private String province;

    private String country;

    private String lastName;

    private String address2;

    private String company;

    private Double latitude;

    private Double longitude;

    private String name;

    private String countryCode;

    private String provinceCode;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
