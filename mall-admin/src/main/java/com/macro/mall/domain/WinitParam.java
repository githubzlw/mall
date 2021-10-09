package com.macro.mall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.macro.mall.domain
 * @date:2021-10-08
 */
@Data
@ApiModel
public class WinitParam {
    @ApiModelProperty("仓库ID,必填(1000008)")
    private String warehouseId;
    @ApiModelProperty("仓库Code,选填(DE0001)")
    private String warehouseCode;
    @ApiModelProperty("商品是否有效,非必填(Y/N)")
    private String isActive;
    @ApiModelProperty("退货库存,非必填(Y/N)")
    private String inReturnInventory;
    @ApiModelProperty("每页显示数量/分页大小,非必填")
    private int pageNum;
    @ApiModelProperty("页码/第几页,非必填")
    private int pageSize;
}
