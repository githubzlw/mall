package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class XmsSourcingInfoParam implements Serializable {

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    @ApiModelProperty(value = "状态：0->已接收；1->处理中；2->已处理 4->取消；5->无效数据； -1->删除；")
    private Integer status;

    @ApiModelProperty(value = "查询的url或者title")
    private String url;

    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @ApiModelProperty(value = "分页行数")
    private Integer pageSize;

    @ApiModelProperty(value = "productId")
    private Long productId;

    @ApiModelProperty(value = "插件的uuid,方便sourcingList的更新")
    private String uuid;

    /*
    @ApiModelProperty(value = "网站类型：1->阿里巴巴；2->速卖通；...")
    private Integer siteType;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "productId")
    private Long productId;*/

}