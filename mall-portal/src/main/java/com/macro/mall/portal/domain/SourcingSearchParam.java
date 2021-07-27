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
public class SourcingSearchParam {

    @ApiModelProperty(value = "搜索内容")
    private String sourcingSearch;

    @ApiModelProperty(value = "ip")
    private String ip;

}