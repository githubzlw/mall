package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息传入的参数
 * Created by zlw on 2021/4/26.
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class XmsMsgParam {

    @ApiModelProperty(value = "会员登录名（邮箱）")
    private String email;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "类型")
    private Integer type;

}