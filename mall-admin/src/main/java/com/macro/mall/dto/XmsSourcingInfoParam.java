package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class XmsSourcingInfoParam implements Serializable {
    private Long id;

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String username;

    @ApiModelProperty(value = "网址")
    private String url;

    @ApiModelProperty(value = "状态：0->已接收；1->处理中；2->已处理 4->取消；5->无效数据； -1->删除；")
    private Integer status;

    @ApiModelProperty(value = "网站类型：1->阿里巴巴；2->速卖通；...")
    private Integer siteType;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @ApiModelProperty(value = "分页行数")
    private Integer pageSize;

}