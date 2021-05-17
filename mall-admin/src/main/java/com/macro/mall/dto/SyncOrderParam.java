package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.dto
 * @date:2021-05-14
 */
@Data
public class SyncOrderParam {

    @ApiModelProperty("客户ID")
    private Long memberId;

    @ApiModelProperty("客户名称")
    private String userName;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("分页数")
    private Integer pageSize;


}
