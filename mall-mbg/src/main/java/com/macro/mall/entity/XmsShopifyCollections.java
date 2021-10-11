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
 * shopify的collections
 * </p>
 *
 * @author jack.luo
 * @since 2021-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsShopifyCollections对象", description="shopify的collections")
public class XmsShopifyCollections implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private String shopName;

    private Long collectionsId;

    private String title;

    private String image;

    private String rules;

    private String collectionJson;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


    private String collKey;

}
