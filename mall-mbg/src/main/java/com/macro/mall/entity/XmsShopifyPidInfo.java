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
 * shopify对应表
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsShopifyPidInfo对象", description="shopify对应表")
public class XmsShopifyPidInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String shopifyName;

    private String shopifyPid;

    private String pid;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private String shopifyInfo;

    @ApiModelProperty(value = "发布状态，0-预发布  1-发布")
    private Integer publish;


}
