package com.macro.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * sourcing表
 * </p>
 *
 * @author jack.luo
 * @since 2021-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XmsSourcingList对象", description="sourcing表")
public class XmsSourcingList implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    @ApiModelProperty(value = "网址")
    private String url;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "价格")
    private String price;

    @ApiModelProperty(value = "运输方式")
    private String shipping;

    @ApiModelProperty(value = "橱窗图")
    private String images;

    @ApiModelProperty(value = "费用")
    private String cost;

    @ApiModelProperty(value = "状态：0->已接收；1->已处理；5->无效数据")
    private Integer status;

    @ApiModelProperty(value = "网站类型：1->阿里巴巴；2->速卖通；...;11 shopify")
    private Integer siteType;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "sku的json数据")
    private String skuJson;

    @ApiModelProperty(value = "生成我司ID")
    private Long productId;

    @ApiModelProperty(value = "货源链接")
    private String sourceLink;


}
