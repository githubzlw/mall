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
 * chrome插件导入表
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsChromeUpload对象", description="chrome插件导入表")
public class XmsChromeUpload implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    @ApiModelProperty(value = "抓取的网址")
    private String url;

    @ApiModelProperty(value = "抓取的标题")
    private String title;

    @ApiModelProperty(value = "抓取的价格")
    private String price;

    @ApiModelProperty(value = "抓取的moq")
    private String moq;

    @ApiModelProperty(value = "抓取的橱窗图")
    private String images;

    @ApiModelProperty(value = "抓取的折扣")
    private String off;

    @ApiModelProperty(value = "抓取的sku")
    private String sku;

    @ApiModelProperty(value = "抓取的产品详情")
    private String productDetail;

    @ApiModelProperty(value = "抓取的产品描述")
    private String productDescription;

    @ApiModelProperty(value = "抓取的运费")
    private String shippingFee;

    @ApiModelProperty(value = "抓取的运输方式")
    private String shippingBy;

    @ApiModelProperty(value = "状态：0->已接收；1->已处理；5->无效数据")
    private Integer status;

    @ApiModelProperty(value = "网站类型")
    private Integer siteType;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
